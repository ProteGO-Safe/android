package pl.gov.mc.protegosafe.domain.usecase.info

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.TimestampsItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateDetailsIfRequiredAndGetResultUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val updateTimestampsIfRequiredAndGetUseCase: UpdateTimestampsIfRequiredAndGetUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(): Single<String> {
        return updateTimestampsIfRequiredAndGetUseCase.execute()
            .observeOn(Schedulers.io())
            .flatMapCompletable(::updateDetailsIfRequired)
            .andThen(covidInfoRepository.getDetails())
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateDetailsIfRequired(timestampsItem: TimestampsItem): Completable {
        return covidInfoRepository
            .getDetailsUpdateTimestamp()
            .flatMapCompletable { localUpdateTimestamp ->
                val isUpdatedRequired = localUpdateTimestamp < timestampsItem.dashboardUpdated
                if (isUpdatedRequired) {
                    updateDetails()
                } else {
                    Completable.complete()
                }
            }
    }

    private fun updateDetails(): Completable {
        return covidInfoRepository
            .fetchDetails()
            .flatMapCompletable(covidInfoRepository::saveDetails)
            .andThen(covidInfoRepository.saveDetailsUpdateTimestamp(getCurrentTimeInSeconds()))
    }
}
