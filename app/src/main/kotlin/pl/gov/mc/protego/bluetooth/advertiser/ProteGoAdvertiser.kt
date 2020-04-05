package pl.gov.mc.protego.bluetooth.advertiser

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import pl.gov.mc.protego.bluetooth.*
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdAgent
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdLocal
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*

class ProteGoAdvertiser(
    private val context: Context,
    private val bluetoothManager: BluetoothManager,
    private val beaconIdAgent: BeaconIdAgent,
    private val advertiserListener: AdvertiserListener
) : AdvertiserInterface, ProteGoGattServerCallback, BeaconIdAgent by beaconIdAgent {

    private val beaconIdAgentListener = object : BeaconIdAgent.Listener {
        override fun useBeaconId(beaconIdLocal: BeaconIdLocal?) {
            this@ProteGoAdvertiser.currentBeaconIdLocal = beaconIdLocal
            this@ProteGoAdvertiser.updatedBeaconIdLocal(beaconIdLocal)
        }
    }

    init {
        beaconIdAgent.registerListener(beaconIdAgentListener)
    }

    // Beacon ID advertised and send by a GATT server.
    private var currentBeaconIdLocal: BeaconIdLocal? = null

    // General enable / disable functions ----------------------------------------------------------
    private var isEnabled = false

    override fun enable(): EnableResult {
        Timber.d("[advertiser] enabling advertiser")
        if (isEnabled) {
            Timber.d("[advertiser] advertiser already enabled")
            return EnableResult.PreconditionFailure.AlreadyEnabled
        }
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Timber.w("[advertiser] failed to get bluetooth adapter")
            return EnableResult.PreconditionFailure.CannotObtainBluetoothAdapter
        }
        if (!bluetoothAdapter.isEnabled) {
            Timber.d("[advertiser] bluetooth is not enabled")
            return EnableResult.PreconditionFailure.BluetoothOff
        }
        isEnabled = true
        val serverResult: ServerResult = startGattServer()
        val advertiserResult: AdvertiserResult = startAdvertising(bluetoothAdapter)
        Timber.d("[advertiser] enabled")
        return EnableResult.Started(advertiserResult, serverResult)
    }

    override fun disable() {
        Timber.d("[advertiser] disabling advertiser")
        if (!isEnabled) {
            Timber.d("[advertiser] advertiser already disabled")
        }
        isEnabled = false
        stopAdvertising()
        stopGattServer()
        Timber.d("[advertiser] disabled")
    }

    // Advertisement -------------------------------------------------------------------------------

    private var isAdvertising = false

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Timber.d("[advertiser] advertisement started")
            isAdvertising = true
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Timber.d("[advertiser] advertisement failed, error=${errorCode}")
            disable()
            advertiserListener.error(this@ProteGoAdvertiser, AdvertiserListener.AdvertiserError.Advertiser(errorCode))
        }
    }

    private fun isAdvertisingSupported(): Boolean {
        return bluetoothManager.adapter.isMultipleAdvertisementSupported
    }

    private fun startAdvertising(bluetoothAdapter: BluetoothAdapter): AdvertiserResult {
        Timber.d("[advertiser] startAdvertising...")
        check(!isAdvertising) { "[advertiser] advertising already pending" }

        if (!isAdvertisingSupported()) {
            Timber.w("[advertiser] advertising is not supported")
            return AdvertiserResult.Failure.NotSupported
        }

        val beaconId = this.currentBeaconIdLocal
        if (beaconId == null || beaconId.expirationDate < Date()) {
            Timber.w("[advertiser] token data is invalid or expired.")
            return AdvertiserResult.Failure.InvalidBeaconId(beaconId)
        }

        val leAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        if (leAdvertiser == null) {
            Timber.w("[advertiser] failed to get leAdvertiser to start advertising")
            return AdvertiserResult.Failure.CannotObtainBluetoothAdvertiser
        }

        val advertiseSettings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW)
            .build()

        // Advertisement data info:
        // - max 31 bytes
        // - 3 bytes for flags.
        // - 2 + X bytes for UUID, where X can be 2,4,16
        // - 2 + 2 + 1 + X bytes for manufacturer data,
        //     where 2 bytes are for company ID, 1 byte for version, X for payload
        val proteGoUUID = UUID.fromString(ProteGoServiceUUIDString)
        val remainingAdvertisementData = 21 - proteGoUUID.advertisementByteLength()

        val beaconIdByteArray = beaconId.beaconId.byteArray

        val data =
            if (beaconIdByteArray.size > remainingAdvertisementData) {
                Timber.w("[advertiser] Token data too long, truncating...")
                val stream = ByteArrayOutputStream(remainingAdvertisementData + 1)
                stream.write(ProteGoManufacturerDataVersion)
                stream.write(beaconIdByteArray, 0, remainingAdvertisementData)
                stream.toByteArray()
            } else {
                val stream = ByteArrayOutputStream(beaconIdByteArray.size + 1)
                stream.write(ProteGoManufacturerDataVersion)
                stream.write(beaconIdByteArray, 0, beaconIdByteArray.size)
                stream.toByteArray()
            }

        val advertiseData = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .setIncludeTxPowerLevel(false)
            .addManufacturerData(ProteGoManufacturerId, data)
            .addServiceUuid(ParcelUuid(UUID.fromString(ProteGoServiceUUIDString)))
            .build()

        leAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
        isAdvertising = true

        return AdvertiserResult.Success
    }

    private fun stopAdvertising() {
        Timber.d("[advertiser] stopAdvertising...")
        if (!isAdvertising) {
            Timber.d("[advertiser] advertising already stopped")
            return
        }
        isAdvertising = false

        val adapter = bluetoothManager.adapter
        if (adapter == null) {
            Timber.w("[advertiser] failed to get adapter to stop advertisement")
            return
        }

        val leAdvertiser = adapter.bluetoothLeAdvertiser
        if (leAdvertiser == null) {
            Timber.w("[advertiser] failed to get leAdvertiser to stop advertisement")
            return
        }

        leAdvertiser.stopAdvertising(advertiseCallback)
    }

    // GATT Server ---------------------------------------------------------------------------------

    private var gattServer: ProteGoGattServer? = null

    private fun startGattServer(): ServerResult {
        Timber.d("[advertiser] starting GATT server...")
        check(gattServer == null) { "[advertiser] GATT server already started" }

        return ProteGoGattServer.startGattServer(context, this, this) { bluetoothManager.openGattServer(context, it) }
            .also {
                when (it) {
                    is ServerResult.Success -> {
                        Timber.d("[advertiser] GATT server start initiated")
                        this.gattServer = it.proteGoGattServer
                    }
                    ServerResult.Failure.CannotObtainGattServer -> Timber.w("[advertiser] failed to open GATT server")
                    ServerResult.Failure.CannotAddService -> Timber.w("[advertiser] failed to initialize ProteGoGattServer")
                    ServerResult.Failure.CannotAddCharacteristic -> Timber.w("[advertiser] failed to initialize ProteGoGattServer")
                }
            }
    }

    private fun stopGattServer() {
        Timber.d("stopping GATT server")
        this.gattServer = this.gattServer.run {
            when (this) {
                null -> Timber.d("[advertiser] GATT server already stopped")
                else -> Timber.d("[advertiser] GATT server stopped successfully: ${close()}")
            }
            null
        }
    }

    override fun gattServerStarted(gattServer: ProteGoGattServer) {
        Timber.i("[advertiser] GATT server started")
        // TODO: Maybe start advertisement here? On the other hand we could fail to
        // start gatt server but managed to start advertisement alone, which is also beneficial.
    }

    override fun gattServerFailed(gattServer: ProteGoGattServer, status: Int) {
        Timber.e("[advertiser] GATT server failed with status: $status")
        disable()
        advertiserListener.error(this, AdvertiserListener.AdvertiserError.Server(status))
    }

    // Token management ----------------------------------------------------------------------------

    fun updatedBeaconIdLocal(beaconIdLocal: BeaconIdLocal?) {

        if (!isEnabled) {
            Timber.d("[advertiser] updated beacon id when not enabled")
            return
        }

        // Reset advertisement data
        stopAdvertising()

        if (beaconIdLocal == null) {
            Timber.d("[advertiser] updated beacon id is null. cannot start new advertisement")
            return
        }

        val expirationDate = beaconIdLocal.expirationDate
        Timber.d("[advertiser] updatedBeaconIdLocal, data: ${beaconIdLocal.beaconId.byteArray.toHexString()}, expirationDate: $expirationDate")

        // Check if expiration date is actually OK.
        if (expirationDate < Date()) {
            Timber.e("[advertiser] received expired beacon id. cannot start new advertisement")
            return
        }

        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            startAdvertising(bluetoothAdapter)
        } else {
            Timber.w("[advertiser] bluetooth adapter is null on updateTokenData")
        }
    }
}