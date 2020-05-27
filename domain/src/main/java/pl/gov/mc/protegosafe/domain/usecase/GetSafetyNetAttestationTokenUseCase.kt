package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.model.exposeNotification.DiagnosisKey

class GetSafetyNetAttestationTokenUseCase(
    private val safetyNetAttestationWrapper: SafetyNetAttestationWrapper,
    private val postExecutionThread: PostExecutionThread
) {
    /**
     * Gets the JSON Web Signature attestation result.
     *
     * @param keys list of [DiagnosisKey]
     * @param regions list of regions.
     *
     * @return [Single] with [String] representing JSON Web Signature attestation result
     */
    fun execute(keys: List<DiagnosisKey>, regions: List<String>): Single<String> {
        return safetyNetAttestationWrapper.attestFor(
            keys = keys,
            regions = regions
        )
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
