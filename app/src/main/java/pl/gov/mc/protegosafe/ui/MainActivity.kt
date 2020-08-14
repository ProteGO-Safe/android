package pl.gov.mc.protegosafe.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protegosafe.BuildConfig
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.ActivityMainBinding
import pl.gov.mc.protegosafe.ui.common.BaseActivity
import pl.gov.mc.protegosafe.ui.common.getSafetyNetErrorAlertDialog
import pl.gov.mc.protegosafe.ui.common.livedata.observe
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class MainActivity : BaseActivity() {

    private val vm: MainViewModel by viewModel()
    private val appUpdateManager: AppUpdateManager by inject()
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
        observeRequests()
        if (BuildConfig.DEBUG) {
            requestDebugModePermissions()
        }
        listenForUpdateEvents()
    }

    private fun observeRequests() {
        vm.showSafetyNetProblem.observe(this, ::showSafetyNetProblem)
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

    private fun listenForUpdateEvents() {
        vm.appUpdateInfoEvent.observe(this, Observer { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    Timber.d("UpdateAvailability is UPDATE_AVAILABLE")
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startUpdateFlow(appUpdateInfo)
                    } else {
                        Timber.d("IMMEDIATE update type is not allowed")
                    }
                }
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    Timber.d("UpdateAvailability is DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS")
                    startUpdateFlow(appUpdateInfo)
                }
                UpdateAvailability.UNKNOWN -> {
                    Timber.d("UpdateAvailability is UNKNOWN")
                }
                UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                    Timber.d("UpdateAvailability is UPDATE_NOT_AVAILABLE")
                }
            }
        })
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            this,
            REQUEST_APP_UPDATES_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_APP_UPDATES_CODE) {
            if (resultCode != RESULT_OK) {
                Timber.e("Update flow failed! Result code: $resultCode")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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

    private fun showSafetyNetProblem() {
        getSafetyNetErrorAlertDialog(this)?.show()
    }
}

private const val REQUEST_STORE_PERMISSION_CODE = 1233
private const val REQUEST_APP_UPDATES_CODE = 1234
