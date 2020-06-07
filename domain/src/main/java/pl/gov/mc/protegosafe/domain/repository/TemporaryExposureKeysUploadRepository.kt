package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single

interface TemporaryExposureKeysUploadRepository {
    fun cacheRequestPayload(payload: String): Completable
    fun getCachedRequestPayload(): Single<String>
    fun clearCache(): Completable
}
