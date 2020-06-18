package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.model.SafetyNetResult
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.repository.SafetyNetCheckRepository
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import java.util.UUID

class CheckDeviceRootedUseCase(
    private val safetyNetCheckRepository: SafetyNetCheckRepository,
    private val safetyNetAttestationWrapper: SafetyNetAttestationWrapper,
    private val deviceRepository: DeviceRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute() =
        safetyNetCheckRepository.isDeviceChecked()
            .flatMap { isDeviceChecked ->
                when {
                    isDeviceChecked -> {
                        Single.just(SafetyNetResult.Success)
                    }
                    deviceRepository.isGooglePlayServicesForSafetyNetAvailable() -> {
                        checkDevice()
                    }
                    else -> {
                        Single.just(SafetyNetResult.Failure.GooglePlayServicesForSafetyNetNotAvailable)
                    }
                }
            }

    /**
     * Generates a 16-byte nonce with additional data.
     * The nonce should also include additional information, such as a user id or any other details
     * you wish to bind to this attestation. Here you can provide a String that is included in the
     * nonce after 24 random bytes. During verification, extract this data again and check it
     * against the request that was made with this nonce.
     */
    private fun generateNonce(data: String): Single<ByteArray> {
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

    private fun checkDevice(): Single<SafetyNetResult> {
        return generateNonce(UUID.randomUUID().toString())
            .flatMap {
                safetyNetAttestationWrapper.attestFor(it)
            }
            .doFinally { safetyNetCheckRepository.setDeviceChecked() }
            .onErrorReturn { SafetyNetResult.Failure.UnknownError(it as? Exception) }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
