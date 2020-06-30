package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.cloud.UploadTemporaryExposureKeysService
import pl.gov.mc.protegosafe.data.mapper.toTemporaryExposureKeysUploadRequestBody
import pl.gov.mc.protegosafe.data.model.GetAccessTokenRequestData
import pl.gov.mc.protegosafe.data.model.RequestBody
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadRequestItem
import pl.gov.mc.protegosafe.domain.repository.TemporaryExposureKeysUploadRepository
import timber.log.Timber

class TemporaryExposureKeysUploadRepositoryImpl(
    private val uploadTemporaryExposureKeysService: UploadTemporaryExposureKeysService
) : TemporaryExposureKeysUploadRepository {
    private var cachedPayload: String? = null
    private var cachedAccessToken: String? = null

    override fun cacheRequestPayload(payload: String): Completable {
        Timber.d("Cache request payload: $payload")
        return Completable.fromAction { cachedPayload = payload }
    }

    override fun cacheRequestAccessToken(accessToken: String): Completable {
        Timber.d("Cache request access token")
        return Completable.fromAction { cachedAccessToken = accessToken }
    }

    override fun getCachedRequestPayload(): Single<String> {
        Timber.d("Get cache request payload: $cachedPayload")
        return cachedPayload?.let { Single.fromCallable { it } }
            ?: Single.error(Exception("Payload cache is empty"))
    }

    override fun getCachedRequestAccessToken(): Single<String> {
        Timber.d("Get cache request payload")
        return cachedAccessToken?.let { Single.fromCallable { it } }
            ?: Single.error(Exception("Payload cache is empty"))
    }

    override fun clearCache(): Completable {
        Timber.d("Clear payload cache")
        return Completable.fromAction {
            cachedPayload = null
            cachedAccessToken = null
        }
    }

    override fun getAccessToken(pinItem: PinItem): Single<String> {
        val requestBody = RequestBody(GetAccessTokenRequestData(pinItem.pin))
        return uploadTemporaryExposureKeysService.getAccessToken(requestBody)
            .subscribeOn(Schedulers.io())
            .map { it.result.token }
    }

    override fun uploadTemporaryExposureKeys(
        requestItem: TemporaryExposureKeysUploadRequestItem
    ): Completable {
        val requestBody = RequestBody(requestItem.toTemporaryExposureKeysUploadRequestBody())
        return uploadTemporaryExposureKeysService.uploadDiagnosisKeys(requestBody)
            .subscribeOn(Schedulers.io())
    }
}
