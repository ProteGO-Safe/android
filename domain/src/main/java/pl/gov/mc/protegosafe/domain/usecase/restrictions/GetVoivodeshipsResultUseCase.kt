package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.VoivodeshipsItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class GetVoivodeshipsResultUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val outgoingBridgeDataResultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(): Single<String> {
        return covidInfoRepository
            .getVoivodeships()
            .map(::getResult)
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(voivodeshipsItem: VoivodeshipsItem): String {
        return outgoingBridgeDataResultComposer.composeDistrictsRestrictionsResult(voivodeshipsItem)
    }
}
