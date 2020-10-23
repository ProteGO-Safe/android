package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Single
import pl.gov.mc.protegosafe.data.db.SafetyNetDataStore
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.repository.SafetyNetRepository
import java.io.ByteArrayOutputStream
import java.security.SecureRandom

class SafetyNetRepositoryImpl(
    private val safetyNetDataStore: SafetyNetDataStore,
    private val safetyNetAttestationWrapper: SafetyNetAttestationWrapper
) : SafetyNetRepository {

    override fun isDeviceChecked(): Single<Boolean> {
        return Single.fromCallable { safetyNetDataStore.isDeviceChecked }
    }

    override fun setDeviceChecked() {
        safetyNetDataStore.isDeviceChecked = true
    }

    override fun getTokenFor(byteArray: ByteArray): Single<String> {
        return safetyNetAttestationWrapper.getTokenFor(byteArray)
    }

    /**
     * Generates a 16-byte nonce with additional data.
     * The nonce should also include additional information, such as a user id or any other details
     * you wish to bind to this attestation. Here you can provide a String that is included in the
     * nonce after 24 random bytes. During verification, extract this data again and check it
     * against the request that was made with this nonce.
     */
    override fun generateNonce(data: String): Single<ByteArray> {
        return Single.fromCallable {
            val byteStream = ByteArrayOutputStream()
            val bytes = ByteArray(24)
            SecureRandom().nextBytes(bytes)
            byteStream.use {
                it.write(bytes)
                it.write(data.toByteArray())
                return@fromCallable it.toByteArray()
            }
        }
    }
}
