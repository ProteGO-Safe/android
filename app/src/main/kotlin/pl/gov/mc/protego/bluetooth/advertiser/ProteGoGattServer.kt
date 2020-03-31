package pl.gov.mc.protego.bluetooth.advertiser

import android.bluetooth.*
import pl.gov.mc.protego.bluetooth.ProteGoCharacteristicUUIDString
import pl.gov.mc.protego.bluetooth.ProteGoServiceUUIDString
import pl.gov.mc.protego.bluetooth.toHexString
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.HashMap

class ProteGoGattServer(
    private val callback: ProteGoGattServerCallback
) : BluetoothGattServerCallback() {

    // This should be nullable as there is potential race condition in the API between callback
    // registration and receiving GattServer instance.
    private var gattServer: BluetoothGattServer? = null

    // Hash map containing pending values for the characteristic.
    private var pendingWrites: HashMap<BluetoothDevice, ByteArray> = HashMap()

    // Lifecycle -----------------------------------------------------------------------------------

    fun initialize(gattServer: BluetoothGattServer): Boolean {
        if (this.gattServer != null ) {
            gattServer.close()
            throw IllegalStateException("Please create a new instance of AnnaGattServer for a new GATT server handle")
        }
        this.gattServer = gattServer

        val gattCharacteristic = BluetoothGattCharacteristic(
            UUID.fromString(ProteGoCharacteristicUUIDString),
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE)

        val gattService = BluetoothGattService(
            UUID.fromString(ProteGoServiceUUIDString),
            BluetoothGattService.SERVICE_TYPE_PRIMARY)

        if (!gattService.addCharacteristic(gattCharacteristic)) {
            Timber.d("Failed to add characteristic")
            gattServer.close()
            return false
        }

        if (!gattServer.addService(gattService)) {
            Timber.d("Failed to add service")
            gattServer.close()
            return false
        }

        return true
    }

    fun close(): Boolean {
        val gattServer = this.gattServer
        if (gattServer == null) {
            Timber.d("GATT server already closed")
            return false
        }

        pendingWrites.clear()
        gattServer.close()
        this.gattServer = null
        Timber.d("GATT server closed")
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
        Timber.d("onDescriptorReadRequest, device=${device?.address}, requestId=${requestId}, offset=${offset}, desc=${descriptor?.uuid}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            Timber.e("invalid onDescriptorReadRequest params")
            return
        }
        gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_READ_NOT_PERMITTED, offset, null)
    }

    override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
        super.onNotificationSent(device, status)
        Timber.d("onNotificationSent, device=${device?.address}, status=${status}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            Timber.e("invalid onNotificationSent params")
            return
        }
    }

    override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
        super.onMtuChanged(device, mtu)
        Timber.d("onMtuChanged, device=${device?.address}, mtu=${mtu}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            Timber.e("invalid onMtuChanged params")
            return
        }
    }

    override fun onPhyUpdate(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyUpdate(device, txPhy, rxPhy, status)
        Timber.d("onPhyUpdate, device=${device?.address}, txPhy=${txPhy}, rxPhy=${rxPhy}, status=${status}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            Timber.e("invalid onPhyUpdate params")
            return
        }
    }

    override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
        super.onExecuteWrite(device, requestId, execute)
        Timber.d("onExecuteWrite, device=${device?.address}, reqId=${requestId}, execute=${execute}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            Timber.e("invalid onExecuteWrite params")
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
                // TODO: Get RSSI somehow?
                this.callback.receivedTokenData(this, pendingWrite, null)
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
        Timber.d("onCharacteristicWriteRequest, device=${device?.address}, reqId=${requestId}, char=${characteristic?.uuid}, prepWrite=${preparedWrite}, responseNeeded=${responseNeeded}, offset=${offset}, value=${value?.toHexString()}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null || characteristic == null || value == null) {
            Timber.e("invalid onCharacteristicWriteRequest params")
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
            // TODO: Get RSSI somehow?
            this.callback.receivedTokenData(this, value, null)
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
        Timber.d("onCharacteristicReadRequest, device: ${device?.address}, reqId=${requestId}, offset=${offset}, char=${characteristic?.uuid}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            Timber.e("invalid onCharacteristicReadRequest params")
            return
        }

        var value: ByteArray? = null
        var status = BluetoothGatt.GATT_READ_NOT_PERMITTED

        val tokenData = this.callback.getTokenData(this)
        if (tokenData != null && offset < tokenData.size) {
            value = tokenData.sliceArray(offset until tokenData.size)
            status = BluetoothGatt.GATT_SUCCESS
        }

        gattServer.sendResponse(device, requestId, status, offset, value)
    }

    override fun onConnectionStateChange(device: BluetoothDevice?, status: Int, newState: Int) {
        super.onConnectionStateChange(device, status, newState)
        Timber.d("onConnectionStateChange, device=${device?.address}, status=${status}, newState=${newState}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            Timber.e("invalid onConnectionStateChange params")
            return
        }
    }

    override fun onPhyRead(device: BluetoothDevice?, txPhy: Int, rxPhy: Int, status: Int) {
        super.onPhyRead(device, txPhy, rxPhy, status)
        Timber.d("onPhyRead, device=${device?.address}, txPhy=${txPhy}, rxPhy=${rxPhy}, status=${status}")
        val gattServer = this.gattServer
        if (gattServer == null || device == null) {
            Timber.e("invalid onPhyRead params")
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
        Timber.d("onDescriptorWriteRequest, device=${device?.address}, reqId=${requestId}, descriptor=${descriptor?.uuid}, prepWrite=${preparedWrite}, responseNeeded=${responseNeeded}, offset=${offset}, value=${value?.toHexString()}")
        val gattServer = this.gattServer
        if (device == null || descriptor == null || gattServer == null) {
            Timber.e("invalid onDescriptorWriteRequest params")
            return
        }
        if (responseNeeded) {
            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_WRITE_NOT_PERMITTED, offset, null)
        }
    }

    override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
        super.onServiceAdded(status, service)
        Timber.d("onServiceAdded status=${status}, service=${service?.uuid}")
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Timber.e("failed to add service: ${service?.uuid ?: "-"}")
            callback.gattServerFailed(this)
            return
        } else {
            callback.gattServerStarted(this)
        }
    }
}