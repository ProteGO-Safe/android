package pl.gov.mc.protegosafe.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protegosafe.BuildConfig
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.ActivityMainBinding
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = vm
        binding.lifecycleOwner = this

        saveNotificationData(intent)
        createNotificationChannel()
        if (BuildConfig.DEBUG) {
            requestDebugModePermissions()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        saveNotificationData(intent)
    }

    private fun saveNotificationData(intent: Intent?) {
        intent?.getStringExtra(Consts.GENERAL_NOTIFICATION_EXTRA_DATA)?.let {
            vm.onNotificationDataReceived(it)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                Consts.GENERAL_NOTIFICATION_CHANNEL_ID,
                getString(R.string.general_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(false)
                enableLights(false)
            }
            (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager?)?.let {
                it.createNotificationChannel(serviceChannel)
                Timber.d("createNotificationChannel: ${serviceChannel.id}")
            }
        }
    }

    private fun requestDebugModePermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.debug_write_store_rationale),
            REQUEST_STORE_PERMISSION_CODE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}

private const val REQUEST_STORE_PERMISSION_CODE = 1233
