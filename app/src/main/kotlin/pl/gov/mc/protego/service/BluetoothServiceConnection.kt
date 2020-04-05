package pl.gov.mc.protego.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.Messenger
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BluetoothServiceConnection(
    val context: Context,
    private val intent: Intent = Intent(context, BluetoothService::class.java),
    private val serviceConnectedLiveData: MutableLiveData<Boolean> = MutableLiveData()
) {

    val serviceConnectedData: LiveData<Boolean>
        get() = serviceConnectedLiveData

    private var serviceMessenger: Messenger? = null

    private val remoteConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            serviceMessenger = Messenger(iBinder)
            serviceConnectedLiveData.value = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            serviceMessenger = null
            serviceConnectedLiveData.value = false
        }
    }

    fun startService() {
        context.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    fun stopService() {
        context.stopService(intent)
    }

    fun bindService(): Boolean =
        context.bindService(intent, remoteConnection, Context.BIND_ABOVE_CLIENT)

    fun unbindService() {
        context.unbindService(remoteConnection)
    }
}