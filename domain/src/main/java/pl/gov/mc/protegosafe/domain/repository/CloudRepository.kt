package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.exposeNotification.TemporaryExposureKeysUploadRequestData

interface CloudRepository {
    fun getAccessToken(pinItem: PinItem): Single<String>
    fun uploadTemporaryExposureKeys(requestData: TemporaryExposureKeysUploadRequestData): Completable
}
