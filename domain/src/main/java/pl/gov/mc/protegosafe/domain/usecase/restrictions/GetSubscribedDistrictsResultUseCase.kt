package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class GetSubscribedDistrictsResultUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return covidInfoRepository.getSortedSubscribedDistricts()
            .flatMap {
                getResult(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(subscribedDistricts: List<DistrictItem>): Single<String> {
        return Single.fromCallable {
            resultComposer.composeSubscribedDistrictsResult(subscribedDistricts)
        }
    }
}
