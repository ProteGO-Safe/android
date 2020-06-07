package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Single

interface SafetyNetCheckRepository {
    fun isDeviceChecked(): Single<Boolean>
    fun setDeviceChecked()
}
