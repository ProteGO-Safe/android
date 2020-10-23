package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.model.SafetyNetResult
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.repository.SafetyNetRepository
import java.util.UUID

class CheckDeviceRootedUseCase(
    private val safetyNetRepository: SafetyNetRepository,
    private val safetyNetAttestationWrapper: SafetyNetAttestationWrapper,
    private val deviceRepository: DeviceRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute() =
        safetyNetRepository.isDeviceChecked()
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
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)

    private fun checkDevice(): Single<SafetyNetResult> {
        return safetyNetRepository.generateNonce(UUID.randomUUID().toString())
            .flatMap {
                safetyNetAttestationWrapper.attestFor(it)
            }
            .doFinally { safetyNetRepository.setDeviceChecked() }
            .onErrorReturn { SafetyNetResult.Failure.UnknownError(it as? Exception) }
    }
}
