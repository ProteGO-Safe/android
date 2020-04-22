package pl.gov.mc.protegosafe.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.ActivityMainBinding
import pl.gov.mc.protegosafe.manager.SafetyNetManager.SafetyNetResult
import pl.gov.mc.protegosafe.ui.dialog.AlertDialogBuilder
import pl.gov.mc.protegosafe.ui.dialog.LoadingDialog
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding

    private val loadingDialog by lazy {
        LoadingDialog.newInstance(getString(R.string.please_wait))
    }
    private var safetyNetAlertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = vm
        binding.lifecycleOwner = this

        saveNotificationData(intent)
        createNotificationChannel()
        observerSafetyNetResult()
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

    private fun observerSafetyNetResult() {
        loadingDialog.show(supportFragmentManager, LoadingDialog.TAG)
        vm.getSafetyNetResultData().observe(this, Observer { result ->
            dismissDialogs()
            handleSafetyNetUi(result)
        })
    }

    private fun dismissDialogs() {
        loadingDialog.dismissAllowingStateLoss()
        safetyNetAlertDialog?.let { dialog ->
            dialog.dismiss()
            safetyNetAlertDialog = null
        }
    }

    private fun handleSafetyNetUi(result: SafetyNetResult) {
        // TODO: Handle SafetyNet UI with PWA.
        when (result) {
            is SafetyNetResult.Failure.ConnectionError -> {
                safetyNetAlertDialog = if (vm.isInternetConnectionAvailable()) {
                    getSafetyNetAlertDialog(result)
                } else {
                    getInternetConnectionDialog()
                }
            }
            is SafetyNetResult.Failure.UpdatePlayServicesError,
            is SafetyNetResult.Failure.SafetyError,
            is SafetyNetResult.Failure.UnknownError ->
                safetyNetAlertDialog = getSafetyNetAlertDialog(result)
        }

        safetyNetAlertDialog?.show()
    }

    private fun getSafetyNetAlertDialog(result: SafetyNetResult): AlertDialog? {
        return AlertDialogBuilder.getSafetyNetErrorAlertDialog(
            context = this,
            result = result,
            onClickListener = DialogInterface.OnClickListener { _, _ ->
                startSafetyNetVerification()
            }
        )
    }

    private fun getInternetConnectionDialog(): AlertDialog {
        return AlertDialogBuilder.getInternetConnectionAlertDialog(
            context = this,
            onClickListener = DialogInterface.OnClickListener { _, _ ->
                startSafetyNetVerification()
            }
        )
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

    private fun startSafetyNetVerification() {
        loadingDialog.show(supportFragmentManager, LoadingDialog.TAG)
        vm.startSafetyNetVerification()
    }
}
