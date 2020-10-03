package pl.gov.mc.protegosafe.domain.manager

import io.reactivex.rxjava3.core.Single
import pl.gov.mc.protegosafe.domain.model.DiagnosisKey
import pl.gov.mc.protegosafe.domain.model.SafetyNetResult

interface SafetyNetAttestationWrapper {
    fun attestFor(byteArray: ByteArray): Single<SafetyNetResult>
    fun attestFor(keys: List<DiagnosisKey>, regions: List<String>): Single<String>
}
