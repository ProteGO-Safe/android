package pl.gov.mc.protegosafe.helpers

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.db.dao.ActivitiesDao
import pl.gov.mc.protegosafe.data.model.RiskCheckActivityDto
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class SetRiskAndAddNotificationHelperUseCase(
    private val remoteConfigurationRepository: RemoteConfigurationRepository,
    private val exposureRepository: ExposureRepository,
    private val activitiesRepository: ActivitiesRepository,
    private val postExecutionThread: PostExecutionThread,
    private val activitiesDao: ActivitiesDao
) {
    fun execute(riskLevel: RiskLevelItem): Single<String> {
        return getRiskLevelConfiguration()
            .flatMapCompletable { riskLevelConfiguration ->
                clearDbAndInsertRiskScore(riskLevel, riskLevelConfiguration)
                    .andThen(saveActivities(riskLevel))
            }
            .toSingle { riskLevel.name }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getRiskLevelConfiguration(): Single<RiskLevelConfigurationItem> {
        return remoteConfigurationRepository.update()
            .andThen(remoteConfigurationRepository.getRiskLevelConfiguration())
    }

    private fun clearDbAndInsertRiskScore(
        riskLevel: RiskLevelItem,
        riskLevelConfiguration: RiskLevelConfigurationItem
    ): Completable {
        return exposureRepository.nukeDb()
            .andThen(
                exposureRepository.upsert(
                    ExposureItem(
                        System.currentTimeMillis(),
                        MAX_EXPOSURE_TIME,
                        getRiskScore(riskLevel, riskLevelConfiguration)
                    )
                )
            )
    }

    private fun getRiskScore(
        riskLevel: RiskLevelItem,
        riskLevelConfiguration: RiskLevelConfigurationItem
    ): Int {
        return when (riskLevel) {
            RiskLevelItem.NO_RISK -> {
                riskLevelConfiguration.maxNoRiskScore
            }
            RiskLevelItem.LOW_RISK -> {
                riskLevelConfiguration.maxLowRiskScore
            }
            RiskLevelItem.MIDDLE_RISK -> {
                riskLevelConfiguration.maxMiddleRiskScore
            }
            else -> {
                riskLevelConfiguration.maxMiddleRiskScore + 1
            }
        }
    }

    private fun saveActivities(riskLevel: RiskLevelItem): Completable {
        return saveExposureActivity(riskLevel)
            .andThen(saveRiskCheckActivity())
    }

    private fun saveExposureActivity(riskLevel: RiskLevelItem): Completable {
        return activitiesRepository.saveExposureCheckActivity(
            ExposureCheckActivityItem(
                riskLevel = riskLevel,
                exposures = Random.nextInt(1, 1000)
            )
        )
    }

    private fun saveRiskCheckActivity(): Completable {
        return activitiesDao.saveRiskCheckActivity(
            RiskCheckActivityDto(
                keys = Random.nextLong(1L, 1000L),
                exposures = Random.nextInt(1, 1000),
                timestamp = getCurrentTimeInSeconds() - TimeUnit.DAYS.toSeconds(
                    Random.nextLong(
                        1L,
                        5L
                    )
                )
            )
        )
    }
}

private const val MAX_EXPOSURE_TIME = 30
