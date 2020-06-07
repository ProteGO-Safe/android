package pl.gov.mc.protegosafe.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.repository.WorkerStateRepository
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler
import timber.log.Timber

class BootCompleteReceiver : BroadcastReceiver(), KoinComponent {

    private val applicationTaskScheduler: ApplicationTaskScheduler by inject()
    private val workerStateRepository: WorkerStateRepository by inject()

    override fun onReceive(context: Context?, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Timber.d("Boot completed received")
            scheduleProvideDiagnosisKeysWorker()
            scheduleRemoveOldExposuresWorker()
        }
    }

    private fun scheduleProvideDiagnosisKeysWorker() {
        val shouldProvideDiagnosisKeysWorkerStartOnBoot =
            workerStateRepository.shouldProvideDiagnosisKeysWorkerStartOnBoot
        Timber.i("Should ProvideDiagnosisKeysWorker start on boot: $shouldProvideDiagnosisKeysWorkerStartOnBoot")
        if (shouldProvideDiagnosisKeysWorkerStartOnBoot) {
            applicationTaskScheduler.scheduleProvideDiagnosisKeysTask()
        }
    }

    private fun scheduleRemoveOldExposuresWorker() {
        applicationTaskScheduler.scheduleRemoveOldExposuresTask()
    }
}
