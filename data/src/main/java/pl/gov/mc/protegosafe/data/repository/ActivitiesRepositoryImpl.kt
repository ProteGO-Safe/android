package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.db.dao.ActivitiesDao
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.mapper.toExposureCheckActivityDto
import pl.gov.mc.protegosafe.data.mapper.toNotificationActivityDto
import pl.gov.mc.protegosafe.data.model.PreAnalyzeDto
import pl.gov.mc.protegosafe.data.model.RiskCheckActivityDto
import pl.gov.mc.protegosafe.domain.model.ActivitiesResultItem
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.model.RiskCheckActivityItem
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import timber.log.Timber

class ActivitiesRepositoryImpl(
    private val activitiesDao: ActivitiesDao
) : ActivitiesRepository {

    override fun getActivitiesResult(): Single<ActivitiesResultItem> {
        return Single.zip(
            getRiskCheckActivities(),
            getExposureCheckActivities(),
            getNotificationActivities(),
            { riskChecks, exposureChecks, notifications ->
                ActivitiesResultItem(
                    riskChecks = riskChecks,
                    exposures = exposureChecks,
                    notifications = notifications
                )
            }
        )
    }

    override fun saveRiskCheckActivity(keysAmount: Long, exposures: Int): Completable {
        Timber.d("Saving risk check(keysAmount=$keysAmount, exposures=$exposures)")
        return activitiesDao.saveRiskCheckActivity(
            RiskCheckActivityDto(keys = keysAmount, exposures = exposures)
        )
    }

    private fun getRiskCheckActivities(): Single<List<RiskCheckActivityItem>> {
        return activitiesDao.getRiskCheckActivities()
            .map { riskCheckActivities ->
                riskCheckActivities.map {
                    it.toEntity()
                }
            }
    }

    override fun saveExposureCheckActivity(
        exposureCheckActivityItem: ExposureCheckActivityItem
    ): Completable {
        Timber.d("Save Exposure Check Activity $exposureCheckActivityItem")
        return activitiesDao.saveExposureCheckActivity(
            exposureCheckActivityItem.toExposureCheckActivityDto()
        )
    }

    private fun getExposureCheckActivities(): Single<List<ExposureCheckActivityItem>> {
        return activitiesDao.getExposureCheckActivities()
            .map { exposureCheckActivities ->
                exposureCheckActivities.map {
                    it.toEntity()
                }
            }
    }

    override fun saveKeysCountToAnalyze(token: String, keysCount: Long): Completable {
        Timber.d("Saving pre-analyze(token=$token, keysCount=$keysCount)")
        return activitiesDao.savePreAnalyze(
            PreAnalyzeDto(
                token = token,
                keysCount = keysCount
            )
        )
    }

    override fun getKeysCountForToken(token: String): Single<Long> {
        return activitiesDao.getKeysCountForToken(token)
    }

    override fun saveNotificationActivity(
        pushNotificationItem: PushNotificationItem
    ): Single<String> {
        return activitiesDao.saveNotificationActivity(pushNotificationItem.toNotificationActivityDto())
    }

    private fun getNotificationActivities(): Single<List<PushNotificationItem>> {
        return activitiesDao.getNotificationActivities()
            .map { notificationActivities ->
                notificationActivities.map {
                    it.toEntity()
                }
            }
    }
}
