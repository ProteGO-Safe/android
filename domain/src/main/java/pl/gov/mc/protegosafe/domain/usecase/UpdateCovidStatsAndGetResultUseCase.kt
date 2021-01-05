package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.CovidStatsItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateCovidStatsAndGetResultUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val outgoingBridgeDataResultComposer: OutgoingBridgeDataResultComposer,
    private val updateCovidStatsUseCase: UpdateCovidStatsUseCase,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return updateCovidStatsUseCase.execute()
            .andThen(
                covidInfoRepository.getCovidStats()
                    .flatMap { getResult(it) }
            )
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(covidStatsItem: CovidStatsItem): Single<String> {
        return Single.fromCallable {
            outgoingBridgeDataResultComposer.composeCovidStatsResult(covidStatsItem)
        }
    }
}
