package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.ActivitiesResultItem
import pl.gov.mc.protegosafe.domain.model.DeleteActivitiesItem
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem

interface ActivitiesRepository {
    fun getActivitiesResult(): Single<ActivitiesResultItem>
    fun saveRiskCheckActivity(keysCount: Long, exposures: Int): Completable
    fun saveExposureCheckActivity(exposureCheckActivityItem: ExposureCheckActivityItem): Completable
    fun saveKeysCountToAnalyze(token: String, keysCount: Long): Completable
    fun getKeysCountForToken(token: String): Single<Long>
    fun saveNotificationActivity(pushNotificationItem: PushNotificationItem): Single<String>
    fun deleteActivities(deleteActivitiesItem: DeleteActivitiesItem): Completable
}
