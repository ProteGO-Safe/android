package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class GetDistrictsRestrictionsUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<List<VoivodeshipItem>> {
        return covidInfoRepository.getDistrictsRestrictions()
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
