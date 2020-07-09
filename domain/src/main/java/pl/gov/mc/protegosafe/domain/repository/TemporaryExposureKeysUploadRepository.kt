package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadRequestItem

interface TemporaryExposureKeysUploadRepository {
    fun getAccessToken(pinItem: PinItem): Single<String>
    fun uploadTemporaryExposureKeys(requestItem: TemporaryExposureKeysUploadRequestItem): Completable
    fun cacheRequestPayload(payload: String): Completable
    fun cacheRequestAccessToken(accessToken: String): Completable
    fun getCachedRequestPayload(): Single<String>
    fun getCachedRequestAccessToken(): Single<String>
    fun clearCache(): Completable
}
