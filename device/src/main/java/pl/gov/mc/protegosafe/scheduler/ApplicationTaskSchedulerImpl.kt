package pl.gov.mc.protegosafe.scheduler

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.RxWorker
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler
import timber.log.Timber

class ApplicationTaskSchedulerImpl(
    private val workManager: WorkManager,
    private val provideDiagnosisKeyWorker: Class<out RxWorker>,
    private val removeOldExposuresWorker: Class<out RxWorker>
) : ApplicationTaskScheduler {

    companion object {
        private const val REPEAT_INTERVAL = 4L
        private val REPEAT_INTERVAL_TIME_UNIT = TimeUnit.HOURS
        const val PROVIDE_DIAGNOSIS_KEYS_WORK_NAME = "PROVIDE_DIAGNOSIS_KEYS_WORK_NAME"
        const val REMOVE_OLD_EXPOSURES_WORK_NAME = "REMOVE_OLD_EXPOSURES_WORK_NAME"
    }

    override fun scheduleProvideDiagnosisKeysTask() {
        // TODO [PSAFE-1007]: Provide Remote Configuration for PeriodicWorkRequest
        Timber.i("scheduleProvideDiagnosisKeysTask")
        val workRequest = PeriodicWorkRequest.Builder(
            provideDiagnosisKeyWorker,
            REPEAT_INTERVAL,
            REPEAT_INTERVAL_TIME_UNIT
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR,
            60L,
            TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            PROVIDE_DIAGNOSIS_KEYS_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    override fun cancelProvideDiagnosisKeysTask() {
        Timber.i("cancelProvideDiagnosisKeysTask")
        workManager.cancelUniqueWork(PROVIDE_DIAGNOSIS_KEYS_WORK_NAME)
    }

    override fun scheduleRemoveOldExposuresTask() {
        Timber.i("scheduleRemoveOldExposuresTask")
        val workRequest = PeriodicWorkRequest.Builder(
            removeOldExposuresWorker,
            1,
            TimeUnit.DAYS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR,
            4,
            TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            REMOVE_OLD_EXPOSURES_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
