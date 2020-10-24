package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.usecase.covidtest.UploadTestPinUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.GetDistrictsRestrictionsResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.GetSubscribedDistrictsResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.HandleDistrictActionUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.UpdateDistrictsRestrictionsUseCase

class OnGetBridgeDataUseCase(
    private val getNotificationDataAndClear: GetNotificationDataAndClearUseCase,
    private val getServicesStatusUseCase: GetServicesStatusUseCase,
    private val getAnalyzeResultUseCase: GetAnalyzeResultUseCase,
    private val getAppVersionNameUseCase: GetAppVersionNameUseCase,
    private val getSystemLanguageUseCase: GetSystemLanguageUseCase,
    private val getFontScaleUseCase: GetFontScaleUseCase,
    private val getDistrictsRestrictionsResultUseCase: GetDistrictsRestrictionsResultUseCase,
    private val updateDistrictsRestrictionsUseCase: UpdateDistrictsRestrictionsUseCase,
    private val handleDistrictActionUseCase: HandleDistrictActionUseCase,
    private val getSubscribedDistrictsResultUseCase: GetSubscribedDistrictsResultUseCase,
    private val uploadTestPinUseCase: UploadTestPinUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(
        type: OutgoingBridgeDataType,
        data: String?,
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
            OutgoingBridgeDataType.GET_FONT_SCALE -> {
                getFontScaleUseCase.execute()
            }
            OutgoingBridgeDataType.DISTRICTS_STATUS -> {
                getDistrictsRestrictionsResultUseCase.execute()
            }
            OutgoingBridgeDataType.UPDATE_DISTRICTS_STATUSES -> {
                updateAndGetDistrictsRestrictionsResult()
            }
            OutgoingBridgeDataType.DISTRICT_ACTION -> {
                data?.let {
                    handleDistrictActionAndGetSubscribedDistrictResult(it)
                } ?: throw IllegalArgumentException()
            }
            OutgoingBridgeDataType.GET_SUBSCRIBED_DISTRICTS -> {
                getSubscribedDistrictsResultUseCase.execute()
            }
            OutgoingBridgeDataType.UPLOAD_COVID_TEST_PIN -> {
                data?.let {
                    uploadTestPinUseCase.execute(it)
                } ?: throw IllegalArgumentException()
            }
            else -> {
                throw IllegalArgumentException("OutgoingBridgeDataType has wrong value")
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateAndGetDistrictsRestrictionsResult(): Single<String> {
        return updateDistrictsRestrictionsUseCase.execute()
            .onErrorComplete()
            .andThen(getDistrictsRestrictionsResultUseCase.execute())
    }

    private fun handleDistrictActionAndGetSubscribedDistrictResult(payload: String): Single<String> {
        return handleDistrictActionUseCase.execute(payload)
            .andThen(getSubscribedDistrictsResultUseCase.execute())
    }
}
