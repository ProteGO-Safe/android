package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.TimestampsItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import pl.gov.mc.protegosafe.domain.usecase.info.UpdateTimestampsIfRequiredAndGetUseCase

class UpdateVoivodeshipsIfRequiredUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val updateTimestampsIfRequiredAndGetUseCase: UpdateTimestampsIfRequiredAndGetUseCase,
    private val updateVoivodeshipsAndSyncDistrictsUseCase: UpdateVoivodeshipsAndSyncDistrictsUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(): Completable {
        return updateTimestampsIfRequiredAndGetUseCase.execute()
            .observeOn(Schedulers.io())
            .flatMapCompletable(::updateVoivodeshipsIfRequired)
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateVoivodeshipsIfRequired(timestampsItem: TimestampsItem): Completable {
        return covidInfoRepository
            .getVoivodeshipsUpdateTimestamp()
            .flatMapCompletable { localUpdateTimestamp ->
                val isUpdatedRequired = localUpdateTimestamp < timestampsItem.districtsUpdated
                if (isUpdatedRequired) {
                    updateVoivodeships()
                } else {
                    Completable.complete()
                }
            }
    }

    private fun updateVoivodeships(): Completable {
        return covidInfoRepository
            .fetchVoivodeships()
            .flatMapCompletable(updateVoivodeshipsAndSyncDistrictsUseCase::execute)
    }
}
