package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.VoivodeshipsItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class GetVoivodeshipsResultOrFetchIfRequiredUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val getVoivodeshipsResultUseCase: GetVoivodeshipsResultUseCase,
    private val updateVoivodeshipsIfRequiredUseCase: UpdateVoivodeshipsIfRequiredUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(): Single<String> {
        return covidInfoRepository
            .getVoivodeships()
            .flatMapCompletable(::updateVoivodeshipsIfRequired)
            .andThen(getVoivodeshipsResultUseCase.execute())
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateVoivodeshipsIfRequired(voivodeshipsItem: VoivodeshipsItem): Completable {
        return if (voivodeshipsItem.items.isEmpty()) {
            updateVoivodeshipsIfRequiredUseCase.execute()
        } else {
            Completable.complete()
        }
    }
}
