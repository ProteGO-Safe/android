package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.ActivityRequest
import pl.gov.mc.protegosafe.domain.model.ActivityResult
import pl.gov.mc.protegosafe.domain.repository.PendingActivityResultRepository

class ProcessPendingActivityResultUseCase(
    private val pendingActivityResultRepository: PendingActivityResultRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(onResultActionRequired: (ActionRequiredItem) -> Unit): Completable =
        pendingActivityResultRepository.hasPendingActivityResult()
            .flatMapCompletable {
                if (it) {
                    processPendingActivityResult(onResultActionRequired)
                } else {
                    Completable.complete()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)

    private fun processPendingActivityResult(onResultActionRequired: (ActionRequiredItem) -> Unit): Completable =
        pendingActivityResultRepository.popActivityResult()
            .flatMapCompletable { activityResult ->
                getActionRequiredItem(activityResult)?.let { onResultActionRequired(it) }
                Completable.complete()
            }

    private fun getActionRequiredItem(activityResult: ActivityResult): ActionRequiredItem? =
        when (activityResult.request) {
            ActivityRequest.ENABLE_BLUETOOTH,
            ActivityRequest.ENABLE_LOCATION,
            ActivityRequest.ENABLE_NOTIFICATIONS -> {
                ActionRequiredItem.SendServicesStatus
            }
            ActivityRequest.START_EXPOSURE_NOTIFICATION -> {
                if (activityResult.isResultOk) {
                    ActionRequiredItem.ExposureNotificationPermissionGranted
                } else {
                    ActionRequiredItem.SendServicesStatus
                }
            }
            ActivityRequest.ACCESS_TEMPORARY_EXPOSURE_KEYS -> {
                if (activityResult.isResultOk) {
                    ActionRequiredItem.TemporaryExposureKeysPermissionGranted
                } else {
                    ActionRequiredItem.TemporaryExposureKeysPermissionDenied
                }
            }
            ActivityRequest.UNKNOWN -> {
                throw Error("Unknown activity result")
            }
            else -> {
                null // No additional action required
            }
        }
}
