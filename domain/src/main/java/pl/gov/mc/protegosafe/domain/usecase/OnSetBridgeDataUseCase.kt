package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType

class OnSetBridgeDataUseCase(
    private val postExecutionThread: PostExecutionThread,
    private val saveTriageCompletedUseCase: SaveTriageCompletedUseCase,
    private val changeServiceStatusUseCase: ChangeServiceStatusUseCase,
    private val clearExposureNotificationDataUseCase: ClearExposureNotificationDataUseCase,
    private val uploadTemporaryExposureKeysUseCase: UploadTemporaryExposureKeysUseCase
) {
    fun execute(input: IncomingBridgeDataItem, onResultActionRequired: (ActionRequiredItem) -> Unit): Completable =
        when (input.type) {
            IncomingBridgeDataType.TRIAGE -> {
                saveTriageCompletedUseCase.execute(input.payload)
            }
            IncomingBridgeDataType.REQUEST_SERVICE_STATUS_CHANGE -> {
                changeServiceStatusUseCase.execute(input.payload, onResultActionRequired)
            }
            IncomingBridgeDataType.REQUEST_CLEAR_EXPOSURE_NOTIFICATIONS_DATA -> {
                clearExposureNotificationDataUseCase.execute(
                    input.payload,
                    onResultActionRequired
                )
            }
            IncomingBridgeDataType.REQUEST_TEMPORARY_EXPOSURE_KEYS_UPLOAD -> {
                uploadTemporaryExposureKeysUseCase.execute(input.payload, onResultActionRequired)
            }
            else -> throw IllegalStateException("Illegal input type")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
}
