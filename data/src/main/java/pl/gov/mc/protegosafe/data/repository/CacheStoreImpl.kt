package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.UIRequest
import pl.gov.mc.protegosafe.domain.repository.CacheStore
import timber.log.Timber

class CacheStoreImpl : CacheStore {
    private var cachedUIRequest: UIRequest? = null

    override fun cacheUiRequest(uiRequest: UIRequest): Completable {
        return Completable.fromAction {
            cachedUIRequest = uiRequest
        }
    }

    override fun getCachedUiRequest(): UIRequest? {
        Timber.d("Get UI request cached: $cachedUIRequest")
        return cachedUIRequest
    }
}
