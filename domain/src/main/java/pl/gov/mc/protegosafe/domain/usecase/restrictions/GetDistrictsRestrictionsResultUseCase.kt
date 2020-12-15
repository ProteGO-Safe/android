package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class GetDistrictsRestrictionsResultUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return covidInfoRepository.getDistrictsRestrictions()
            .zipWith(
                covidInfoRepository.getVoivodeshipsUpdateTimestamp()
            ) { voivodeships, timestamp ->
                getResult(voivodeships, timestamp)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(voivodeships: List<VoivodeshipItem>, updated: Long): String {
        return resultComposer.composeDistrictsRestrictionsResult(voivodeships, updated)
    }
}
