package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Single

interface SafetyNetRepository {
    fun isDeviceChecked(): Single<Boolean>
    fun setDeviceChecked()
    fun generateNonce(data: String): Single<ByteArray>
    fun getTokenFor(byteArray: ByteArray): Single<String>
}
