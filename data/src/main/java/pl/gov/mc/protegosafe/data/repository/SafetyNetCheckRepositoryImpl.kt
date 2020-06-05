package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Single
import pl.gov.mc.protegosafe.data.db.SafetyNetDataStore
import pl.gov.mc.protegosafe.domain.repository.SafetyNetCheckRepository

class SafetyNetCheckRepositoryImpl(
    private val safetyNetDataStore: SafetyNetDataStore
) : SafetyNetCheckRepository {

    override fun isDeviceChecked(): Single<Boolean> {
        return Single.fromCallable { safetyNetDataStore.isDeviceChecked }
    }

    override fun setDeviceChecked() {
        safetyNetDataStore.isDeviceChecked = true
    }
}
