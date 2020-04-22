package pl.gov.mc.protegosafe.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.ActivityMainBinding
import pl.gov.mc.protegosafe.domain.usecase.StartBLEMonitoringServiceUseCase
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = vm
        binding.lifecycleOwner = this

        saveNotificationData(intent)
        createNotificationChannel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        saveNotificationData(intent)
    }

    private fun saveNotificationData(intent: Intent?) {
        intent?.getStringExtra(Consts.NOTIFICATION_EXTRA_DATA)?.let {
            vm.onNotificationDataReceived(it)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                Consts.NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
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

    //TODO add permissions and battery optimizations and check ble support
}
