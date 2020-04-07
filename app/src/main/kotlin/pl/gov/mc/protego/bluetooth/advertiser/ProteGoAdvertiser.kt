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
) : AdvertiserInterface, ProteGoGattServerCallback {

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

    private fun timberWithLocalTag() = Timber.tag("[advertiser]")

    override fun enable(): EnableResult {
        timberWithLocalTag().d("enabling advertiser")
        if (isEnabled) {
            timberWithLocalTag().d("advertiser already enabled")
            return EnableResult.PreconditionFailure.AlreadyEnabled
        }
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            timberWithLocalTag().w("failed to get bluetooth adapter")
            return EnableResult.PreconditionFailure.CannotObtainBluetoothAdapter
        }
        if (!bluetoothAdapter.isEnabled) {
            timberWithLocalTag().d("bluetooth is not enabled")
            return EnableResult.PreconditionFailure.BluetoothOff
        }
        isEnabled = true
        val serverResult: ServerResult = startGattServer()
        val advertiserResult: AdvertiserResult = startAdvertising(bluetoothAdapter)
        timberWithLocalTag().d("enabled")
        return EnableResult.Started(advertiserResult, serverResult)
    }

    override fun disable() {
        timberWithLocalTag().d("disabling advertiser")
        if (!isEnabled) {
            timberWithLocalTag().d("advertiser already disabled")
        }
        isEnabled = false
        stopAdvertising()
        stopGattServer()
        timberWithLocalTag().d("disabled")
    }

    // Advertisement -------------------------------------------------------------------------------

    private var isAdvertising = false

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            timberWithLocalTag().d("advertisement started")
            isAdvertising = true
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            timberWithLocalTag().d("advertisement failed, error=${errorCode}")
            disable()
            advertiserListener.error(this@ProteGoAdvertiser, AdvertiserListener.AdvertiserError.Advertiser(errorCode))
        }
    }

    private fun isAdvertisingSupported(): Boolean {
        return bluetoothManager.adapter.isMultipleAdvertisementSupported
    }

    private fun startAdvertising(bluetoothAdapter: BluetoothAdapter): AdvertiserResult {
        timberWithLocalTag().d("startAdvertising...")
        check(!isAdvertising) { "[advertiser] advertising already pending" }

        if (!isAdvertisingSupported()) {
            timberWithLocalTag().w("advertising is not supported")
            return AdvertiserResult.Failure.NotSupported
        }

        val beaconId = this.currentBeaconIdLocal
        if (beaconId == null || beaconId.expirationDate < Date()) {
            timberWithLocalTag().w("token data is invalid or expired.")
            return AdvertiserResult.Failure.InvalidBeaconId(beaconId)
        }

        val leAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        if (leAdvertiser == null) {
            timberWithLocalTag().w("failed to get leAdvertiser to start advertising")
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

        val beaconIdByteArray = beaconId.id.byteArray

        val data =
            if (beaconIdByteArray.size > remainingAdvertisementData) {
                timberWithLocalTag().w("Token data too long, truncating...")
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
        timberWithLocalTag().d("stopAdvertising...")
        if (!isAdvertising) {
            timberWithLocalTag().d("advertising already stopped")
            return
        }
        isAdvertising = false

        val adapter = bluetoothManager.adapter
        if (adapter == null) {
            timberWithLocalTag().w("failed to get adapter to stop advertisement")
            return
        }

        val leAdvertiser = adapter.bluetoothLeAdvertiser
        if (leAdvertiser == null) {
            timberWithLocalTag().w("failed to get leAdvertiser to stop advertisement")
            return
        }

        leAdvertiser.stopAdvertising(advertiseCallback)
    }

    // GATT Server ---------------------------------------------------------------------------------

    private var gattServer: ProteGoGattServer? = null

    private fun startGattServer(): ServerResult {
        timberWithLocalTag().d("starting GATT server...")
        check(gattServer == null) { "[advertiser] GATT server already started" }

        return ProteGoGattServer.startGattServer(context, beaconIdAgent, this) { bluetoothManager.openGattServer(context, it) }
            .also {
                when (it) {
                    is ServerResult.Success -> {
                        timberWithLocalTag().d("GATT server start initiated")
                        this.gattServer = it.proteGoGattServer
                    }
                    ServerResult.Failure.CannotObtainGattServer -> timberWithLocalTag().w("failed to open GATT server")
                    ServerResult.Failure.CannotAddService -> timberWithLocalTag().w("failed to initialize ProteGoGattServer")
                    ServerResult.Failure.CannotAddCharacteristic -> timberWithLocalTag().w("failed to initialize ProteGoGattServer")
                }
            }
    }

    private fun stopGattServer() {
        timberWithLocalTag().d("stopping GATT server")
        this.gattServer = this.gattServer.run {
            when (this) {
                null -> timberWithLocalTag().d("GATT server already stopped")
                else -> timberWithLocalTag().d("GATT server stopped successfully: ${close()}")
            }
            null
        }
    }

    override fun gattServerStarted(gattServer: ProteGoGattServer) {
        timberWithLocalTag().i("GATT server started")
        // TODO: Maybe start advertisement here? On the other hand we could fail to
        // start gatt server but managed to start advertisement alone, which is also beneficial.
    }

    override fun gattServerFailed(gattServer: ProteGoGattServer, status: Int) {
        timberWithLocalTag().e("GATT server failed with status: $status")
        disable()
        advertiserListener.error(this, AdvertiserListener.AdvertiserError.Server(status))
    }

    // Token management ----------------------------------------------------------------------------

    private fun updatedBeaconIdLocal(beaconIdLocal: BeaconIdLocal?) {

        if (!isEnabled) {
            timberWithLocalTag().d("updated beacon id when not enabled")
            return
        }

        // Reset advertisement data
        stopAdvertising()

        if (beaconIdLocal == null) {
            timberWithLocalTag().d("updated beacon id is null. cannot start new advertisement")
            return
        }

        val expirationDate = beaconIdLocal.expirationDate
        timberWithLocalTag().d("updatedBeaconIdLocal, data: ${beaconIdLocal.id.byteArray.toHexString()}, expirationDate: $expirationDate")

        // Check if expiration date is actually OK.
        if (expirationDate < Date()) {
            timberWithLocalTag().e("received expired beacon id. cannot start new advertisement")
            return
        }

        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            startAdvertising(bluetoothAdapter)
        } else {
            timberWithLocalTag().w("bluetooth adapter is null on updateTokenData")
        }
    }
}