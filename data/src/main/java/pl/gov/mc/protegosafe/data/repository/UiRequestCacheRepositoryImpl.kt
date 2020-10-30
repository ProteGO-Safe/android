package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.GetBridgeDataUIRequestItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.ResultStatus
import pl.gov.mc.protegosafe.domain.model.SetBridgeDataUIRequestItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.model.UIRequest
import pl.gov.mc.protegosafe.domain.repository.CacheStore
import pl.gov.mc.protegosafe.domain.repository.UiRequestCacheRepository
import pl.gov.mc.protegosafe.domain.usecase.UploadTemporaryExposureKeysWithCachedPayloadUseCase
import timber.log.Timber

class UiRequestCacheRepositoryImpl(
    private val outgoingBridgeDataResultComposer: OutgoingBridgeDataResultComposer,
    private val uploadTemporaryExposureKeysWithCachedPayloadUseCase: UploadTemporaryExposureKeysWithCachedPayloadUseCase,
    private val cacheStore: CacheStore
) : UiRequestCacheRepository {

    override fun getCachedRequest(): UIRequest? {
        return cacheStore.getCachedUiRequest()
    }

    override fun retryCachedRequest(
        uiRequest: GetBridgeDataUIRequestItem,
        getBridgeData: (dataType: Int, data: String, requestId: String) -> Unit
    ) {
        getBridgeData(uiRequest.bridgeDataType.code, uiRequest.payload, uiRequest.requestId)
    }

    override fun retryCachedRequest(
        uiRequest: SetBridgeDataUIRequestItem,
        onResultActionRequired: (actionRequired: ActionRequiredItem) -> Unit
    ): Completable {
        return when (uiRequest.bridgeDataType) {
            IncomingBridgeDataType.REQUEST_TEMPORARY_EXPOSURE_KEYS_UPLOAD -> {
                uploadTemporaryExposureKeysWithCachedPayloadUseCase.execute(onResultActionRequired)
            }
            else -> Completable.fromAction {
                Timber.d("No need to handle request")
            }
        }
    }

    override fun cancelCachedRequest(
        uiRequest: GetBridgeDataUIRequestItem,
        bridgeDataResponse: (body: String, dataType: Int, requestId: String) -> Unit
    ) {
        when (uiRequest.bridgeDataType) {
            OutgoingBridgeDataType.UPLOAD_COVID_TEST_PIN -> {
                bridgeDataResponse(
                    outgoingBridgeDataResultComposer.composeUploadTestPinResult(ResultStatus.CANCELED),
                    uiRequest.bridgeDataType.code,
                    uiRequest.requestId
                )
            }
            else -> Timber.d("No need to handle request")
        }
    }

    override fun cancelCachedRequest(
        uiRequest: SetBridgeDataUIRequestItem,
        onBridgeData: (dataType: Int, dataJson: String) -> Unit
    ) {
        when (uiRequest.bridgeDataType) {
            IncomingBridgeDataType.REQUEST_TEMPORARY_EXPOSURE_KEYS_UPLOAD -> {
                onBridgeData(
                    OutgoingBridgeDataType.TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS.code,
                    outgoingBridgeDataResultComposer.composeTemporaryExposureKeysUploadResult(
                        TemporaryExposureKeysUploadState.CANCELED
                    )
                )
            }
            else -> Timber.d("No need to handle request")
        }
    }
}
