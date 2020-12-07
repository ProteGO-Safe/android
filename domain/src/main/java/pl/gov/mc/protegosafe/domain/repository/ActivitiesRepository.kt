package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.ActivitiesResultItem
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem

interface ActivitiesRepository {
    fun saveRiskCheckActivity(keysAmount: Long, exposures: Int): Completable
    fun getActivitiesResult(): Single<ActivitiesResultItem>
    fun saveExposureCheckActivity(exposureCheckActivityItem: ExposureCheckActivityItem): Completable
    fun saveKeysCountToAnalyze(token: String, keysCount: Long): Completable
    fun getKeysCountForToken(token: String): Single<Long>
}
