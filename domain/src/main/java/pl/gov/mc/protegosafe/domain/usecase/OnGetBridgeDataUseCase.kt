package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType

class OnGetBridgeDataUseCase(
    private val getNotificationDataAndClear: GetNotificationDataAndClearUseCase,
    private val getServicesStatusUseCase: GetServicesStatusUseCase,
    private val getAnalyzeResultUseCase: GetAnalyzeResultUseCase,
    private val getAppVersionNameUseCase: GetAppVersionNameUseCase,
    private val getSystemLanguageUseCase: GetSystemLanguageUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(
        type: OutgoingBridgeDataType
    ): Single<String> {
        return when (type) {
            OutgoingBridgeDataType.NOTIFICATION_DATA -> {
                Single.fromCallable { getNotificationDataAndClear.execute() }
            }
            OutgoingBridgeDataType.SERVICES_STATUS -> {
                getServicesStatusUseCase.execute()
            }
            OutgoingBridgeDataType.ANALYZE_RESULT -> {
                getAnalyzeResultUseCase.execute()
            }
            OutgoingBridgeDataType.APP_VERSION -> {
                getAppVersionNameUseCase.execute()
            }
            OutgoingBridgeDataType.SYSTEM_LANGUAGE -> {
                getSystemLanguageUseCase.execute()
            }
            else -> {
                throw IllegalArgumentException("OutgoingBridgeDataType has wrong value")
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
