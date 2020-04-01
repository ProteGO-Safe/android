package pl.gov.mc.protego.bluetooth.advertiser

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Handler
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference


/**
 * Class for getting RSSI of an (presumably) already connected [BluetoothDevice]
 * Note: Apparently when a [BluetoothGatt] is opened [android.bluetooth.BluetoothGattServer.cancelConnection] does not disconnect
 * the [BluetoothDevice] – [BluetoothGatt.disconnect] needs to be called in order to actually disconnect
 */
class ProteGoGattRSSILatch(context: Context, bluetoothDevice: BluetoothDevice, private val bluetoothHandler: Handler) {

    var rssi: Int? = null
        private set

    private val shouldDisconnect = AtomicBoolean(false)
    private val currentState = AtomicInteger(BluetoothProfile.STATE_DISCONNECTED)
    private val gattReference = AtomicReference<BluetoothGatt>()

    init {
        val bluetoothGattCallback = object : BluetoothGattCallback() {
            override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
                super.onReadRemoteRssi(gatt, rssi, status)
                Timber.i("${gatt.device} RSSI: $rssi")
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    this@ProteGoGattRSSILatch.rssi = rssi
                }
            }

            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                currentState.set(newState)
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    if (shouldDisconnect.get()) {
                        bluetoothHandler.post { gatt.disconnect() }
                    } else {
                        gattReference.set(gatt)
                        bluetoothHandler.post { gatt.readRemoteRssi() }
                    }
                    return
                }
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // should eventually reach this state
                    bluetoothHandler.post { gatt.close() }
                }
            }
        }
        bluetoothHandler.post {
            gattReference.compareAndSet(null, bluetoothDevice.connectGatt(context, true, bluetoothGattCallback))
        }
    }

    fun cancel() {
        if (shouldDisconnect.compareAndSet(false, true)) {
            gattReference.get()?.run {
                bluetoothHandler.post {
                    disconnect()
                    if (currentState.get() == BluetoothProfile.STATE_DISCONNECTED) {
                        close()
                    }
                }
            }
        }
    }
}