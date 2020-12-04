package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem

interface ActivitiesRepository {
    fun saveRiskCheckActivity(keysAmount: Long): Completable
    fun saveExposureCheckActivity(exposureCheckActivityItem: ExposureCheckActivityItem): Completable
}
