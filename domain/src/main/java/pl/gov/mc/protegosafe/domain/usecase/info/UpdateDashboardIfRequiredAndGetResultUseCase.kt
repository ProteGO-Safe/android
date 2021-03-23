package pl.gov.mc.protegosafe.domain.usecase.info

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.TimestampsItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateDashboardIfRequiredAndGetResultUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val updateTimestampsIfRequiredAndGetUseCase: UpdateTimestampsIfRequiredAndGetUseCase,
    private val updateDashboardUseCase: UpdateDashboardUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(): Single<String> {
        return updateTimestampsIfRequiredAndGetUseCase.execute()
            .observeOn(Schedulers.io())
            .flatMapCompletable(::updateDashboardIfRequired)
            .andThen(covidInfoRepository.getDashboard())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateDashboardIfRequired(timestampsItem: TimestampsItem): Completable {
        return covidInfoRepository
            .getDashboardUpdateTimestamp()
            .flatMapCompletable { localUpdateTimestamp ->
                val isUpdatedRequired = localUpdateTimestamp < timestampsItem.dashboardUpdated
                if (isUpdatedRequired) {
                    updateDashboard()
                } else {
                    Completable.complete()
                }
            }
            .observeOn(Schedulers.io())
    }

    private fun updateDashboard(): Completable {
        return covidInfoRepository
            .fetchDashboard()
            .flatMapCompletable(updateDashboardUseCase::execute)
    }
}
