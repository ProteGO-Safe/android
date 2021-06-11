package pl.gov.mc.protegosafe.domain.manager

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.SafetyNetResult

interface SafetyNetAttestationWrapper {
    fun attestFor(byteArray: ByteArray): Single<SafetyNetResult>
}
