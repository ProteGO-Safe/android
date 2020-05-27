package pl.gov.mc.protegosafe.domain.manager

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.exposeNotification.DiagnosisKey

interface SafetyNetAttestationWrapper {
    fun attestFor(keys: List<DiagnosisKey>, regions: List<String>): Single<String>
}
