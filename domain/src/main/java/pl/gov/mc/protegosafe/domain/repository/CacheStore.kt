package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.UIRequest

interface CacheStore {
    fun cacheUiRequest(uiRequest: UIRequest): Completable
    fun getCachedUiRequest(): UIRequest?
}
