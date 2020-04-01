package pl.gov.mc.protego.bluetooth.advertiser

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import pl.gov.mc.protego.bluetooth.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*

class ProteGoAdvertiser(
    private val context: Context,
    private val bluetoothManager: BluetoothManager,
    private val listener: AdvertiserListener
) : AdvertiserInterface, ProteGoGattServerCallback {

    // Advertisement timer task
    private var advertisementTimer: Timer? = null

    // Token data advertised and send by a GATT server.
    private var tokenData: Pair<ByteArray, Date>? = null

    // General enable / disable functions ----------------------------------------------------------
    private var isEnabled = false

    fun enable(): EnableResult {
        Timber.d("enabling advertiser")
        if (isEnabled) {
            Timber.d("advertiser already enabled")
            return EnableResult.PreconditionFailure.AlreadyEnabled
        }
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Timber.w("failed to get bluetooth adapter")
            return EnableResult.PreconditionFailure.CannotObtainBluetoothAdapter
        }
        if (!bluetoothAdapter.isEnabled) {
            Timber.d("bluetooth is not enabled")
            return EnableResult.PreconditionFailure.BluetoothOff
        }
        isEnabled = true
        val serverResult: ServerResult = startGattServer()
        val advertiserResult: AdvertiserResult = startAdvertising(bluetoothAdapter)
        return EnableResult.Started(advertiserResult, serverResult)
    }

    fun disable() {
        Timber.d("disabling advertiser")
        if (!isEnabled) {
            Timber.d("advertiser already disabled")
        }
        isEnabled = false
        stopAdvertising()
        stopGattServer()
    }

    // Advertisement -------------------------------------------------------------------------------

    private var isAdvertising = false

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Timber.d("advertisement started")
            isAdvertising = true
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Timber.d("advertisement failed, error=${errorCode}")
            isAdvertising = false
        }
    }

    private fun isAdvertisingSupported(): Boolean {
        return bluetoothManager.adapter.isMultipleAdvertisementSupported
    }

    private fun startAdvertising(bluetoothAdapter: BluetoothAdapter): AdvertiserResult {
        Timber.d("startAdvertising")
        check(!isAdvertising) { "advertising already pending" }

        if (!isAdvertisingSupported()) {
            Timber.w("advertising is not supported")
            return AdvertiserResult.Failure.NotSupported
        }

        val tokenData = this.tokenData
        if (tokenData == null || tokenData.second < Date()) {
            Timber.w("token data is invalid or expired.")
            listener.tokenDataExpired(this, tokenData)
            return AdvertiserResult.Failure.InvalidToken(tokenData)
        }

        val leAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        if (leAdvertiser == null) {
            Timber.w("failed to get leAdvertiser to start advertising")
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

        val data =
            if (tokenData.first.size > remainingAdvertisementData) {
                Timber.w("Token data too long, truncating...")
                val stream = ByteArrayOutputStream(remainingAdvertisementData + 1)
                stream.write(ProteGoManufacturerDataVersion)
                stream.write(tokenData.first, 0, remainingAdvertisementData)
                stream.toByteArray()
            } else {
                val stream = ByteArrayOutputStream(tokenData.first.size + 1)
                stream.write(ProteGoManufacturerDataVersion)
                stream.write(tokenData.first, 0, tokenData.first.size)
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

        // Setup advertisement timer based on the expiration date
        var timer = this.advertisementTimer
        if (timer != null) {
            timer.cancel()
            timer.purge()
        }

        timer = Timer("ProteGoAdvertisementTimer")
        val hopefullyCurrentLooper = Looper.myLooper() ?: Looper.getMainLooper().also {
            Timber.w("Cannot obtain current thread looper")
        }
        val handler = Handler(hopefullyCurrentLooper)
        timer.schedule(object : TimerTask() {
            override fun run() {
                Timber.d("Advertisement timer fired")
                // TODO: Make sure that it's on the same thread as a normal operation.
                handler.post { tokenDataExpired() }
            }
        }, tokenData.second)
        this.advertisementTimer = timer
        return AdvertiserResult.Success
    }

    private fun stopAdvertising() {
        Timber.d("stopAdvertising")
        if (!isAdvertising) {
            Timber.d("advertising already stopped")
            return
        }
        isAdvertising = false

        val timer = this.advertisementTimer
        if (timer != null) {
            timer.cancel()
            timer.purge()
            this.advertisementTimer = null
        }

        val adapter = bluetoothManager.adapter
        if (adapter == null) {
            Timber.w("failed to get adapter to stop advertisement")
            return
        }

        val leAdvertiser = adapter.bluetoothLeAdvertiser
        if (leAdvertiser == null) {
            Timber.w("failed to get leAdvertiser to stop advertisement")
            return
        }

        leAdvertiser.stopAdvertising(advertiseCallback)
    }

    // GATT Server ---------------------------------------------------------------------------------

    private var gattServer: ProteGoGattServer? = null

    private fun startGattServer(): ServerResult {
        Timber.d("starting GATT server...")
        check(gattServer == null) { "GATT server already started" }

        return ProteGoGattServer.startGattServer(this) { bluetoothManager.openGattServer(context, it) }
            .also {
                when(it) {
                    is ServerResult.Success -> {
                        Timber.d("GATT server start initiated")
                        this.gattServer = it.proteGoGattServer
                    }
                    ServerResult.Failure.CannotObtainGattServer -> Timber.w("failed to open GATT server")
                    ServerResult.Failure.CannotAddService -> Timber.w("failed to initialize ProteGoGattServer")
                    ServerResult.Failure.CannotAddCharacteristic -> Timber.w("failed to initialize ProteGoGattServer")
                }
            }
    }

    private fun stopGattServer() {
        Timber.d("stopping GATT server")
        val gattServer = this.gattServer
        if (gattServer == null) {
            Timber.d("GATT server already stopped")
            return
        }

        gattServer.close()
        this.gattServer = null
        Timber.d("gatt server stopped")
        return
    }

    override fun gattServerStarted(gattServer: ProteGoGattServer) {
        Timber.d("GATT server started")
        // TODO: Maybe start advertisement here? On the other hand we could fail to
        // start gatt server but managed to start advertisement alone, which is also beneficial.
    }

    override fun gattServerFailed(gattServer: ProteGoGattServer) {
        Timber.d("GATT server failed")
        stopGattServer()
    }

    // Token management ----------------------------------------------------------------------------

    private fun tokenDataExpired() {
        Timber.d("token data expired")
        val previousToken = this.tokenData
        this.tokenData = null
        listener.tokenDataExpired(this, previousToken)

        val tokenData = this.tokenData
        if (tokenData == null) {
            Timber.w("failed to get new token data, stopping advertisement")
            stopAdvertising()
        }
    }

    override fun updateTokenData(data: ByteArray, expirationDate: Date) {
        Timber.d("updateTokenData, data: ${data.toHexString()}, expirationDate: $expirationDate")
        this.tokenData = Pair(data, expirationDate)

        if (!isEnabled) {
            Timber.d("received token data when not enabled")
            return
        }

        // Check if expiration date is actually OK.
        if (expirationDate < Date()) {
            Timber.e("received expired token data")
            stopAdvertising()
            // Don't call `tokenDataExpired` to prevent potential recursion.
            return
        }

        // Reset advertisement data
        stopAdvertising()
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            startAdvertising(bluetoothAdapter)
        } else {
            Timber.w("bluetooth adapter is null on updateTokenData")
        }
    }

    override fun getTokenData(gattServer: ProteGoGattServer): ByteArray? {
        Timber.d("getTokenData")
        val tokenData = this.tokenData
        if (tokenData == null || tokenData.second < Date()) {
            this.listener.tokenDataExpired(this, tokenData)
        }
        return this.tokenData?.first
    }

    override fun receivedTokenData(
        gattServer: ProteGoGattServer,
        byteArray: ByteArray,
        rssi: Int?
    ) {
        Timber.d("receivedTokenData, byteArray: ${byteArray.toHexString()}, rssi: $rssi")
        this.listener.synchronizedTokenData(this, byteArray, rssi)
    }
}