package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.rxjava3.core.Single

interface SafetyNetCheckRepository {
    fun isDeviceChecked(): Single<Boolean>
    fun setDeviceChecked()
}
