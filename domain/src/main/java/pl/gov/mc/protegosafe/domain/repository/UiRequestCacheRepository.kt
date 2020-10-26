package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.GetBridgeDataUIRequestItem
import pl.gov.mc.protegosafe.domain.model.SetBridgeDataUIRequestItem
import pl.gov.mc.protegosafe.domain.model.UIRequest

interface UiRequestCacheRepository {
    fun cacheRequest(uiRequest: UIRequest): Completable
    fun getCachedRequest(): UIRequest?
    fun retryCachedRequest(
        uiRequest: GetBridgeDataUIRequestItem,
        getBridgeData: (dataType: Int, data: String, requestId: String) -> Unit
    )

    fun retryCachedRequest(
        uiRequest: SetBridgeDataUIRequestItem,
        onResultActionRequired: (actionRequired: ActionRequiredItem) -> Unit
    ): Completable

    fun cancelCachedRequest(
        uiRequest: GetBridgeDataUIRequestItem,
        bridgeDataResponse: (body: String, dataType: Int, requestId: String) -> Unit
    )

    fun cancelCachedRequest(
        uiRequest: SetBridgeDataUIRequestItem,
        onBridgeData: (dataType: Int, dataJson: String) -> Unit
    )
}
