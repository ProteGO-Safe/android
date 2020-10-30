package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.Date
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getExposureLastValidDate
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository

class GetAnalyzeResultUseCase(
    private val exposureRepository: ExposureRepository,
    private val postExecutionThread: PostExecutionThread,
    private val remoteConfigurationRepository: RemoteConfigurationRepository,
    private val covidInfoRepository: CovidTestRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer
) {
    fun execute(): Single<String> {
        return exposureRepository.getAllResults()
            .subscribeOn(Schedulers.io())
            .map { listOfExposures ->
                listOfExposures
                    .filter { !Date(it.date).before(getExposureLastValidDate()) }
                    .maxByOrNull { it.riskScore }
                    ?: ExposureItem(
                        System.currentTimeMillis(),
                        NO_EXPOSURE_DEFAULT_VALUE,
                        NO_EXPOSURE_DEFAULT_VALUE
                    )
            }
            .flatMap { exposure ->
                getRiskLevelConfiguration()
                    .flatMap {
                        exposureRepository.getRiskLevel(it, exposure)
                    }
                    .flatMap { riskLevel ->
                        clearCovidTestDataIfNecessary(riskLevel)
                            .andThen(
                                getResult(riskLevel)
                            )
                    }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getRiskLevelConfiguration(): Single<RiskLevelConfigurationItem> {
        return remoteConfigurationRepository.update()
            .andThen(remoteConfigurationRepository.getRiskLevelConfiguration())
    }

    private fun clearCovidTestDataIfNecessary(riskLevelItem: RiskLevelItem): Completable {
        return if (riskLevelItem != RiskLevelItem.HIGH_RISK) {
            covidInfoRepository.clearCovidTestData()
        } else {
            Completable.complete()
        }
    }

    private fun getResult(riskLevelItem: RiskLevelItem): Single<String> {
        return Single.fromCallable {
            resultComposer.composeAnalyzeResult(riskLevelItem)
        }
    }
}

private const val NO_EXPOSURE_DEFAULT_VALUE = 0
