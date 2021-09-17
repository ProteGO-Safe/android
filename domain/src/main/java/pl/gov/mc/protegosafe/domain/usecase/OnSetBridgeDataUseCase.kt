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
    private val clearDataUseCase: ClearDataUseCase,
    private val uploadTemporaryExposureKeysUseCase: UploadTemporaryExposureKeysUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
    private val closeAppUseCase: CloseAppUseCase,
    private val appReviewUseCase: AppReviewUseCase,
    private val deleteActivitiesUseCase: DeleteActivitiesUseCase,
    private val sendSmsAppUseCase: SendSmsAppUseCase
) {
    fun execute(input: IncomingBridgeDataItem, onResultActionRequired: (ActionRequiredItem) -> Unit): Completable =
        when (input.type) {
            IncomingBridgeDataType.TRIAGE -> {
                saveTriageCompletedUseCase.execute(input.payload)
            }
            IncomingBridgeDataType.REQUEST_SERVICE_STATUS_CHANGE -> {
                changeServiceStatusUseCase.execute(input.payload, onResultActionRequired)
            }
            IncomingBridgeDataType.REQUEST_CLEAR_DATA -> {
                clearDataUseCase.execute(input.payload, onResultActionRequired)
            }
            IncomingBridgeDataType.REQUEST_TEMPORARY_EXPOSURE_KEYS_UPLOAD -> {
                uploadTemporaryExposureKeysUseCase.execute(input.payload, onResultActionRequired)
            }
            IncomingBridgeDataType.SYSTEM_LANGUAGE -> {
                setAppLanguageUseCase.execute(input.payload, onResultActionRequired)
            }
            IncomingBridgeDataType.CLOSE_APPLICATION -> {
                closeAppUseCase.execute(input.payload, onResultActionRequired)
            }
            IncomingBridgeDataType.APP_REVIEW -> {
                appReviewUseCase.execute(input.payload, onResultActionRequired)
            }
            IncomingBridgeDataType.DELETE_ACTIVITIES -> {
                deleteActivitiesUseCase.execute(input.payload)
            }
            IncomingBridgeDataType.SEND_SMS -> {
                sendSmsAppUseCase.execute(input.payload, onResultActionRequired)
            }
            else -> throw IllegalStateException("Illegal input type")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
}
