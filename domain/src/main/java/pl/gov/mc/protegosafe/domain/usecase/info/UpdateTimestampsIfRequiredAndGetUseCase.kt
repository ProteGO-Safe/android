package pl.gov.mc.protegosafe.domain.usecase.info

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.TimestampsItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import pl.gov.mc.protegosafe.domain.utils.isTimestampBeforeNow

class UpdateTimestampsIfRequiredAndGetUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(): Single<TimestampsItem> {
        return updateTimestampsIfRequired()
            .andThen(covidInfoRepository.getTimestamps())
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateTimestampsIfRequired(): Completable {
        return covidInfoRepository
            .getTimestamps()
            .flatMapCompletable { oldTimestamps ->
                val isUpdatedRequired = isTimestampBeforeNow(oldTimestamps.nextUpdate)
                if (isUpdatedRequired) {
                    updateTimestamps()
                } else {
                    Completable.complete()
                }
            }
    }

    private fun updateTimestamps(): Completable {
        return covidInfoRepository
            .fetchTimestamps()
            .flatMapCompletable(covidInfoRepository::saveTimestamps)
    }
}
