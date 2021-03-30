package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateVoivodeshipsAndSyncDistrictsUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val notifyDistrictsUpdatedUseCase: NotifyDistrictsUpdatedUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(voivodeshipsJson: String): Completable {
        return covidInfoRepository
            .saveVoivodeships(voivodeshipsJson)
            .andThen(updateData())
            .flatMapCompletable { state ->
                when (state) {
                    is VoivodeshipsUpdateState.NoNewData -> handleNoNewData()
                    is VoivodeshipsUpdateState.InitialData -> handleInitialData(state.voivodeships)
                    is VoivodeshipsUpdateState.NewData -> handleNewData(state.voivodeships)
                }
            }
            .andThen(updateTimestamp())
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateData(): Single<VoivodeshipsUpdateState> {
        return Single.zip(
            covidInfoRepository.getVoivodeships(),
            covidInfoRepository.getVoivodeshipsUpdateTimestamp(),
            covidInfoRepository.getTimestamps()
        ) { voivodeships, localUpdateTimestamp, timestamps ->
            when {
                localUpdateTimestamp == 0L ->
                    VoivodeshipsUpdateState.InitialData(voivodeships.items)
                localUpdateTimestamp < timestamps.districtsUpdated ->
                    VoivodeshipsUpdateState.NewData(voivodeships.items)
                else -> VoivodeshipsUpdateState.NoNewData
            }
        }
    }

    private fun handleNoNewData(): Completable = updateTimestamp()

    private fun handleInitialData(voivodeships: List<VoivodeshipItem>): Completable = syncDistricts(voivodeships)

    private fun handleNewData(voivodeships: List<VoivodeshipItem>): Completable {
        return syncDistricts(voivodeships).andThen(notifyUserAboutDistrictsUpdate(voivodeships))
    }

    private fun syncDistricts(voivodeships: List<VoivodeshipItem>): Completable {
        val districts = voivodeships.flatMapTo(mutableListOf(), VoivodeshipItem::districts)
        return covidInfoRepository.syncDistrictsRestrictionsWithDb(districts)
    }

    private fun updateTimestamp(): Completable {
        return covidInfoRepository.saveVoivodeshipsUpdateTimestamp(getCurrentTimeInSeconds())
    }

    private fun notifyUserAboutDistrictsUpdate(voivodeships: List<VoivodeshipItem>): Completable {
        val districts = voivodeships.flatMapTo(mutableListOf(), VoivodeshipItem::districts)
        return notifyDistrictsUpdatedUseCase.execute(districts)
    }

    private sealed class VoivodeshipsUpdateState {
        object NoNewData : VoivodeshipsUpdateState()
        data class InitialData(val voivodeships: List<VoivodeshipItem>) : VoivodeshipsUpdateState()
        data class NewData(val voivodeships: List<VoivodeshipItem>) : VoivodeshipsUpdateState()
    }
}
