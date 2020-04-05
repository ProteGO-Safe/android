package pl.gov.mc.protego.bluetooth.advertiser

import android.bluetooth.*
import android.content.Context
import pl.gov.mc.protego.bluetooth.*
import pl.gov.mc.protego.bluetooth.beacon.*
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap

class ProteGoGattServer private constructor(
    private val context: Context,
    private val beaconIdAgent: BeaconIdAgent,
    private val callback: ProteGoGattServerCallback
) : BluetoothGattServerCallback() {

    private var beaconIdLocal: BeaconIdLocal? = null

    private val beaconIdAgentListener = object : BeaconIdAgent.Listener {
        override fun useBeaconId(beaconIdLocal: BeaconIdLocal?) {
            this@ProteGoGattServer.beaconIdLocal = beaconIdLocal
        }
    }

    init {
        beaconIdAgent.registerListener(beaconIdAgentListener)
    }

    companion object {
        fun startGattServer(
            context: Context,
            beaconIdAgent: BeaconIdAgent,
            proteGoGattServerCallback: ProteGoGattServerCallback,
            gattServerCreator: (BluetoothGattServerCallback) -> BluetoothGattServer?
        ): ServerResult {
            val proteGoGattServer = ProteGoGattServer(context, beaconIdAgent, proteGoGattServerCallback)
            val gattServer = gattServerCreator(proteGoGattServer)
            if (gattServer == null) {
                timberWithLocalTag().w("failed to open GATT server")
                return ServerResult.Failure.CannotObtainGattServer
            }
            return proteGoGattServer.initialize(gattServer) ?: ServerResult.Success(proteGoGattServer)
        }

        private fun timberWithLocalTag() = Timber.tag("[server]")
    }

    // This should be nullable as there is potential race condition in the API between callback
    // registration and receiving GattServer instance.
    private var gattServer: BluetoothGattServer? = null

    // Hash map containing pending values for the characteristic.
    private var pendingWrites: HashMap<BluetoothDevice, ByteArray> = HashMap()

    // Hash map containing RSSI grabbers.
    private var rssiLatches: HashMap<BluetoothDevice, ProteGoGattRSSILatch> = HashMap()

    private var bluetoothHandler = safeCurrentThreadHandler()

    private fun BluetoothDevice.latchRssi() {
        rssiLatches[this] = ProteGoGattRSSILatch(context, this, bluetoothHandler)
    }

    private fun BluetoothDevice.getLatchedRssi() = rssiLatches[this]?.rssi

    // Lifecycle -----------------------------------------------------------------------------------

    private fun initialize(gattServer: BluetoothGattServer): ServerResult.Failure? {
        check(this.gattServer == null) {
            gattServer.close()
            "[server] Please create a new instance of ProteGoGattServer for a new GATT server handle"
        }
        this.gattServer = gattServer

        val gattCharacteristic = BluetoothGattCharacteristic(
            UUID.fromString(ProteGoCharacteristicUUIDString),
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        val gattService = BluetoothGattService(
            UUID.fromString(ProteGoServiceUUIDString),
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        if (!gattService.addCharacteristic(gattCharacteristic)) {
            timberWithLocalTag().d("Failed to add characteristic")
            gattServer.close()
            return ServerResult.Failure.CannotAddCharacteristic
        }

        if (!gattServer.addService(gattService)) {
            timberWithLocalTag().d("Failed to add service")
            gattServer.close()
            return ServerResult.Failure.CannotAddService
        }

        timberWithLocalTag().i("GATT server initialized")
        return null
    }

    fun close(): Boolean {
        val gattServer = this.gattServer
        if (gattServer == null) {
            timberWithLocalTag().d("GATT server already closed")
            return false
        }

        pendingWrites.clear()
        rssiLatches.values.forEach { it.cancel() }
        rssiLatches.clear()
        gattServer.close()
        this.gattServer = null
        timberWithLocalTag().i("GATT server closed")
        return true
    }

    // GATT Server callbacks -----------------------------------------------------------------------

    override fun onDescriptorReadRequest(
        device: BluetoothDevice?,
        requestId: Int,
        offset: Int,
        descriptor: BluetoothGattDescriptor?
    ) {
        super.onDescriptorReadRequest(device, requestId, offset, descriptor)
        timberWithLocalTag().d("onDescriptorReadRequest, device=${device?.address}, requestId=${requestId}, offset=${offset}, desc=${descriptor?.uuid}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            timberWithLocalTag().e("invalid onDescriptorReadRequest params")
            return
        }
        gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_READ_NOT_PERMITTED, offset, null)
    }

    override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
        super.onNotificationSent(device, status)
        timberWithLocalTag().d("onNotificationSent, device=${device?.address}, status=${status}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            timberWithLocalTag().e("invalid onNotificationSent params")
            return
        }
    }

    override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
        super.onMtuChanged(device, mtu)
        timberWithLocalTag().d("onMtuChanged, device=${device?.address}, mtu=${mtu}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            timberWithLocalTag().e("invalid onMtuChanged params")
            return
        }
    }

    override fun onPhyUpdate(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyUpdate(device, txPhy, rxPhy, status)
        timberWithLocalTag().d("onPhyUpdate, device=${device?.address}, txPhy=${txPhy}, rxPhy=${rxPhy}, status=${status}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            timberWithLocalTag().e("invalid onPhyUpdate params")
            return
        }
    }

    override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
        super.onExecuteWrite(device, requestId, execute)
        timberWithLocalTag().d("onExecuteWrite, device=${device?.address}, reqId=${requestId}, execute=${execute}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            timberWithLocalTag().e("invalid onExecuteWrite params")
            return
        }

        var value: ByteArray? = null
        var status = BluetoothGatt.GATT_WRITE_NOT_PERMITTED

        if (execute) {
            // Let's execute pending writes.
            val pendingWrite = this.pendingWrites.remove(device)
            if (pendingWrite != null) {
                status = BluetoothGatt.GATT_SUCCESS
                value = pendingWrite
                val beaconIdRemote = BeaconIdRemote(
                    BeaconId(value, ProteGoManufacturerDataVersion),
                    device.getLatchedRssi(),
                    BeaconIdSource.CONNECTION_INCOMING
                )
                this.beaconIdAgent.synchronizedBeaconId(beaconIdRemote)
            }
        } else {
            // We always allow cancelling request.
            this.pendingWrites.remove(device)
        }

        gattServer.sendResponse(device, requestId, status, 0, value)
    }

    override fun onCharacteristicWriteRequest(
        device: BluetoothDevice?,
        requestId: Int,
        characteristic: BluetoothGattCharacteristic?,
        preparedWrite: Boolean,
        responseNeeded: Boolean,
        offset: Int,
        value: ByteArray?
    ) {
        super.onCharacteristicWriteRequest(
            device,
            requestId,
            characteristic,
            preparedWrite,
            responseNeeded,
            offset,
            value
        )
        timberWithLocalTag().d("onCharacteristicWriteRequest, device=${device?.address}, reqId=${requestId}, char=${characteristic?.uuid}, prepWrite=${preparedWrite}, responseNeeded=${responseNeeded}, offset=${offset}, value=${value?.toHexString()}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null || characteristic == null || value == null) {
            timberWithLocalTag().e("invalid onCharacteristicWriteRequest params")
            return
        }

        var status = BluetoothGatt.GATT_WRITE_NOT_PERMITTED

        if (preparedWrite) {
            // Got prepared write
            val pendingValue = this.pendingWrites[device]
            val expectedOffset = (pendingValue?.size ?: 0)
            if (expectedOffset == offset) {
                val stream = ByteArrayOutputStream(expectedOffset + value.size)
                if (pendingValue != null) {
                    stream.write(pendingValue)
                }
                stream.write(value)
                this.pendingWrites[device] = stream.toByteArray()
                status = BluetoothGatt.GATT_SUCCESS
            }
        } else {
            // Got simple write.
            status = BluetoothGatt.GATT_SUCCESS
            val beaconIdRemote =
                BeaconIdRemote(BeaconId(value, ProteGoManufacturerDataVersion), device.getLatchedRssi(), BeaconIdSource.CONNECTION_INCOMING)
            this.beaconIdAgent.synchronizedBeaconId(beaconIdRemote)
        }

        if (responseNeeded) {
            gattServer.sendResponse(device, requestId, status, offset, value)
        }
    }

    override fun onCharacteristicReadRequest(
        device: BluetoothDevice?,
        requestId: Int,
        offset: Int,
        characteristic: BluetoothGattCharacteristic?
    ) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
        timberWithLocalTag().d("onCharacteristicReadRequest, device: ${device?.address}, reqId=${requestId}, offset=${offset}, char=${characteristic?.uuid}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            timberWithLocalTag().e("invalid onCharacteristicReadRequest params")
            return
        }

        var value: ByteArray? = null
        var status = BluetoothGatt.GATT_READ_NOT_PERMITTED

        val beaconIdLocalByteArray = this.beaconIdLocal?.beaconId?.byteArray
        if (beaconIdLocalByteArray != null && offset < beaconIdLocalByteArray.size) {
            value = beaconIdLocalByteArray.sliceArray(offset until beaconIdLocalByteArray.size)
            status = BluetoothGatt.GATT_SUCCESS
        }

        gattServer.sendResponse(device, requestId, status, offset, value)
    }

    override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
        super.onConnectionStateChange(device, status, newState)
        timberWithLocalTag().d("onConnectionStateChange, device=${device?.address}, status=${status}, newState=${newState}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            timberWithLocalTag().e("invalid onConnectionStateChange params")
            return
        }
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            this.pendingWrites.remove(device)
            this.rssiLatches.remove(device)
        }
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            device.latchRssi()
        }
    }

    override fun onPhyRead(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyRead(device, txPhy, rxPhy, status)
        timberWithLocalTag().d("onPhyRead, device=${device?.address}, txPhy=${txPhy}, rxPhy=${rxPhy}, status=${status}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            timberWithLocalTag().e("invalid onPhyRead params")
            return
        }
    }

    override fun onDescriptorWriteRequest(
        device: BluetoothDevice?,
        requestId: Int,
        descriptor: BluetoothGattDescriptor?,
        preparedWrite: Boolean,
        responseNeeded: Boolean,
        offset: Int,
        value: ByteArray?
    ) {
        super.onDescriptorWriteRequest(
            device,
            requestId,
            descriptor,
            preparedWrite,
            responseNeeded,
            offset,
            value
        )
        timberWithLocalTag().d("onDescriptorWriteRequest, device=${device?.address}, reqId=${requestId}, descriptor=${descriptor?.uuid}, prepWrite=${preparedWrite}, responseNeeded=${responseNeeded}, offset=${offset}, value=${value?.toHexString()}")
        val gattServer = this.gattServer
        if (device == null || descriptor == null || gattServer == null) {
            timberWithLocalTag().e("invalid onDescriptorWriteRequest params")
            return
        }
        if (responseNeeded) {
            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_WRITE_NOT_PERMITTED, offset, null)
        }
    }

    override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
        super.onServiceAdded(status, service)
        timberWithLocalTag().d("onServiceAdded status=${status}, service=${service?.uuid}")
        if (status != BluetoothGatt.GATT_SUCCESS) {
            timberWithLocalTag().e("failed to add service: ${service?.uuid ?: "-"}")
            callback.gattServerFailed(this, status)
            return
        } else {
            callback.gattServerStarted(this)
        }
    }
}