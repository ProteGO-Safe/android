package pl.gov.mc.protegosafe.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.usecase.StartBLEMonitoringServiceUseCase
import timber.log.Timber

class BootCompleteReceiver : BroadcastReceiver(), KoinComponent {

    private val startBLEMonitoringServiceUseCase: StartBLEMonitoringServiceUseCase by inject()

    override fun onReceive(context: Context?, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Timber.d("Boot completed received")
            try {
                Timber.d("Attempting to start service")
                startBLEMonitoringServiceUseCase.execute(START_BLE_MONITOR_SERVICE_DELAY)
            } catch (e: Throwable) {
                Timber.e(e, "StartOnBootReceiver")
            }
        }
    }

    companion object {
        private const val START_BLE_MONITOR_SERVICE_DELAY = 500L
    }
}