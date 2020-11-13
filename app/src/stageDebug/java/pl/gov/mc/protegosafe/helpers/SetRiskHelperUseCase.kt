package pl.gov.mc.protegosafe.helpers

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository

class SetRiskHelperUseCase(
    private val remoteConfigurationRepository: RemoteConfigurationRepository,
    private val exposureRepository: ExposureRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(riskLevel: RiskLevelItem): Single<String> {
        return getRiskLevelConfiguration()
            .flatMapCompletable { riskLevelConfiguration ->
                clearDbAndInsertRiskScore(riskLevel, riskLevelConfiguration)
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
}
private const val MAX_EXPOSURE_TIME = 30
