package pl.gov.mc.protegosafe.domain.usecase.info

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateDashboardUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(dashboardJson: String): Completable {
        return covidInfoRepository
            .saveDashboard(dashboardJson)
            .andThen(updateTimestamp())
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateTimestamp(): Completable {
        return covidInfoRepository.saveDashboardUpdateTimestamp(getCurrentTimeInSeconds())
    }
}
