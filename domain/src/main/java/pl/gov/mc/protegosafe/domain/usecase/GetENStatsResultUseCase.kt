package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ENStatsItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class GetENStatsResultUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val outgoingBridgeDataResultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return covidInfoRepository.getENStats()
            .flatMap {
                getResult(it)
            }
            .onErrorResumeNext {
                if (it is NullPointerException) {
                    getResult(null)
                } else {
                    throw it
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(enStatsItem: ENStatsItem?): Single<String> {
        return Single.fromCallable {
            outgoingBridgeDataResultComposer.composeENStatsResult(enStatsItem)
        }
    }
}
