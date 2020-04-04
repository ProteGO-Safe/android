package pl.gov.mc.protego.service

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.splash.SplashScreenActivity

class BluetoothService : Service() {

    private val bluetoothManager: BluetoothManager by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val powerService: PowerManager by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onBind(intent: Intent): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("WakelockTimeout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(startId, createServiceRunningNotification())

        wakeLock = powerService.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            WAKE_LOCK_TAG
        ).apply {
            acquire()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        wakeLock.release()
        super.onDestroy()
    }

    private fun createServiceRunningNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerServiceNotificationChannel()
        }
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        val pendingIntent = PendingIntent
            .getActivity(
                applicationContext, 0,
                Intent(
                    applicationContext,
                    SplashScreenActivity::class.java
                ),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setContentTitle(getString(R.string.bluetooth_service_running_title))
            .setContentText(getString(R.string.bluetooth_service_running_description))
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerServiceNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    companion object {
        const val WAKE_LOCK_TAG = "ProtegoApp:BluetoothService"
        const val CHANNEL_ID = "BluetoothServiceChannel"
    }
}
