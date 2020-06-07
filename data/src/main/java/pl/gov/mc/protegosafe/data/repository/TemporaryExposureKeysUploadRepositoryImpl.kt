package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.TemporaryExposureKeysUploadRepository
import timber.log.Timber

class TemporaryExposureKeysUploadRepositoryImpl : TemporaryExposureKeysUploadRepository {
    private var cachedPayload: String? = null

    override fun cacheRequestPayload(payload: String): Completable {
        Timber.d("Cache request payload: $payload")
        return Completable.fromAction { cachedPayload = payload }
    }

    override fun getCachedRequestPayload(): Single<String> {
        Timber.d("Get cache request payload: $cachedPayload")
        return cachedPayload?.let { Single.fromCallable { it } }
            ?: Single.error(Exception("Payload cache is empty"))
    }

    override fun clearCache(): Completable {
        Timber.d("Clear payload cache")
        return Completable.fromAction { cachedPayload = null }
    }
}
