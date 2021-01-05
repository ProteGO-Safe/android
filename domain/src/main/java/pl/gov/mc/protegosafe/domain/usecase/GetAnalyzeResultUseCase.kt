package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository

class GetAnalyzeResultUseCase(
    private val exposureRepository: ExposureRepository,
    private val postExecutionThread: PostExecutionThread,
    private val covidInfoRepository: CovidTestRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer
) {
    fun execute(): Single<String> {
        return exposureRepository.getAllResults()
            .subscribeOn(Schedulers.io())
            .flatMap { listOfExposures ->
                exposureRepository.getMaxExposureOrDefault(listOfExposures)
            }
            .flatMap { exposure ->
                exposureRepository.calcRiskLevel(exposure)
                    .flatMap { riskLevel ->
                        clearCovidTestDataIfNecessary(riskLevel)
                            .andThen(getResult(riskLevel))
                    }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
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
