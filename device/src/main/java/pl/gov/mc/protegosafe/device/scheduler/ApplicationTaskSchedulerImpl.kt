package pl.gov.mc.protegosafe.device.scheduler

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.RxWorker
import androidx.work.WorkManager
import pl.gov.mc.protegosafe.domain.repository.AppRepository
import java.util.concurrent.TimeUnit
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler
import timber.log.Timber

class ApplicationTaskSchedulerImpl(
    private val appRepository: AppRepository,
    private val workManager: WorkManager,
    private val provideDiagnosisKeyWorker: Class<out RxWorker>,
    private val removeOldExposuresWorker: Class<out RxWorker>,
    private val updateDistrictsRestrictionsWorker: Class<out RxWorker>
) : ApplicationTaskScheduler {

    companion object {
        private const val REPEAT_INTERVAL = 4L
        private val REPEAT_INTERVAL_TIME_UNIT = TimeUnit.HOURS
        const val PROVIDE_DIAGNOSIS_KEYS_WORK_NAME = "PROVIDE_DIAGNOSIS_KEYS_WORK_NAME"
        const val REMOVE_OLD_EXPOSURES_WORK_NAME = "REMOVE_OLD_EXPOSURES_WORK_NAME"
        const val UPDATE_DISTRICTS_RESTRICTIONS_WORK_NAME = "UPDATE_DISTRICTS_RESTRICTIONS_WORK_NAME"
    }

    private val repeatIntervalInMinutes by lazy {
        appRepository.getWorkersIntervalInMinutes()
    }

    override fun scheduleProvideDiagnosisKeysTask(shouldReplaceExistingWork: Boolean) {
        Timber.i("scheduleProvideDiagnosisKeysTask")
        val existingPeriodicWorkPolicy = if (shouldReplaceExistingWork) {
            ExistingPeriodicWorkPolicy.REPLACE
        } else {
            ExistingPeriodicWorkPolicy.KEEP
        }
        val workRequest = PeriodicWorkRequest.Builder(
            provideDiagnosisKeyWorker,
            repeatIntervalInMinutes,
            REPEAT_INTERVAL_TIME_UNIT
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR,
            1L,
            TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            PROVIDE_DIAGNOSIS_KEYS_WORK_NAME,
            existingPeriodicWorkPolicy,
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
            1L,
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

    override fun scheduleUpdateDistrictsRestrictionsTask() {
        Timber.i("scheduleUpdateDistrictsRestrictionsTask")
        val workRequest = PeriodicWorkRequest.Builder(
            updateDistrictsRestrictionsWorker,
            repeatIntervalInMinutes,
            REPEAT_INTERVAL_TIME_UNIT
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).setBackoffCriteria(
            BackoffPolicy.LINEAR,
            1,
            TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            UPDATE_DISTRICTS_RESTRICTIONS_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
