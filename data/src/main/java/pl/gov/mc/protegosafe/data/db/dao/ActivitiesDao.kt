package pl.gov.mc.protegosafe.data.db.dao

import doTransaction
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.model.ExposureCheckActivityDto
import pl.gov.mc.protegosafe.data.model.PreAnalyzeDto
import pl.gov.mc.protegosafe.data.model.NotificationActivityDto
import pl.gov.mc.protegosafe.data.model.RiskCheckActivityDto
import queryAllAsSingle
import timber.log.Timber

open class ActivitiesDao {
    fun saveRiskCheckActivity(riskCheckActivityDto: RiskCheckActivityDto): Completable {
        Timber.d("Saving risk check activity $riskCheckActivityDto")
        return doTransaction {
            it.copyToRealmOrUpdate(riskCheckActivityDto)
        }
    }

    fun getRiskCheckActivities(): Single<List<RiskCheckActivityDto>> {
        return queryAllAsSingle()
    }

    fun deleteRiskCheckByID(riskCheckIDs: List<String>): Completable {
        return doTransaction { realm ->
            realm.where(RiskCheckActivityDto::class.java).findAll()
                .filter { riskCheckIDs.contains(it.id) }
                .forEach { it.deleteFromRealm() }
        }
    }

    fun saveExposureCheckActivity(exposureCheckActivityDto: ExposureCheckActivityDto): Completable {
        Timber.d("Saving exposure check activity $exposureCheckActivityDto")
        return doTransaction {
            it.copyToRealmOrUpdate(exposureCheckActivityDto)
        }
    }

    fun getExposureCheckActivities(): Single<List<ExposureCheckActivityDto>> {
        return queryAllAsSingle()
    }

    fun deleteExposureChecksByID(exposureCheckIDs: List<String>): Completable {
        return doTransaction { realm ->
            realm.where(ExposureCheckActivityDto::class.java).findAll()
                .filter { exposureCheckIDs.contains(it.id) }
                .forEach { it.deleteFromRealm() }
        }
    }

    fun savePreAnalyze(preAnalyzeDto: PreAnalyzeDto): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(preAnalyzeDto)
        }
    }

    fun getKeysCountForToken(token: String): Single<Long> {
        return queryAllAsSingle<PreAnalyzeDto>()
            .map { preAnalyzes ->
                preAnalyzes.firstOrNull {
                    it.token == token
                }?.keysCount
            }
    }

    fun getPreAnalyzes(): Single<List<PreAnalyzeDto>> {
        return queryAllAsSingle()
    }

    fun saveNotificationActivity(notificationActivityDto: NotificationActivityDto): Single<String> {
        return doTransaction {
            it.copyToRealmOrUpdate(notificationActivityDto)
        }.toSingle {
            notificationActivityDto.id
        }
    }

    fun getNotificationActivities(): Single<List<NotificationActivityDto>> {
        return queryAllAsSingle()
    }

    fun deleteNotificationActivitiesByID(notificationIDs: List<String>): Completable {
        return doTransaction { realm ->
            realm.where(NotificationActivityDto::class.java).findAll()
                .filter { notificationIDs.contains(it.id) }
                .forEach { it.deleteFromRealm() }
        }
    }
}
