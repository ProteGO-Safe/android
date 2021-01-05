package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.model.CovidInfoItem
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateDistrictsRestrictionsUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val notifyDistrictsUpdatedUseCase: NotifyDistrictsUpdatedUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    private sealed class DistrictsUpdateState {
        object NoNewData : DistrictsUpdateState()
        class InitialData(val covidInfo: CovidInfoItem) : DistrictsUpdateState()
        class NewData(val covidInfo: CovidInfoItem) : DistrictsUpdateState()
    }

    fun execute(covidInfo: CovidInfoItem? = null): Completable {
        return updateData(covidInfo)
            .flatMapCompletable { updateState ->
                when (updateState) {
                    is DistrictsUpdateState.NoNewData -> {
                        Completable.complete()
                    }
                    is DistrictsUpdateState.InitialData -> {
                        handleInitialData(updateState.covidInfo)
                    }
                    is DistrictsUpdateState.NewData -> {
                        handleNewData(updateState.covidInfo)
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateData(covidInfoItem: CovidInfoItem?): Single<DistrictsUpdateState> {
        return getCovidInfo(covidInfoItem)
            .zipWith<CovidInfoItem, Long, DistrictsUpdateState>(
                covidInfoRepository.getVoivodeshipsUpdateTimestamp()
            ) { covidInfo: CovidInfoItem, updatedTimeStamp: Long ->
                when {
                    covidInfo.voivodeshipsUpdated == updatedTimeStamp -> {
                        DistrictsUpdateState.NoNewData
                    }
                    updatedTimeStamp == 0L -> {
                        DistrictsUpdateState.InitialData(covidInfo)
                    }
                    else -> {
                        DistrictsUpdateState.NewData(covidInfo)
                    }
                }
            }
    }

    private fun getCovidInfo(covidInfo: CovidInfoItem?): Single<CovidInfoItem> {
        return if (covidInfo == null) {
            covidInfoRepository.getCovidInfo()
        } else {
            Single.just(covidInfo)
        }
    }

    private fun handleInitialData(covidInfo: CovidInfoItem): Completable {
        return syncWithDbAndSaveTimestamp(covidInfo)
    }

    private fun handleNewData(covidInfo: CovidInfoItem): Completable? {
        return notifyUserAboutDistrictsUpdate(covidInfo)
            .andThen(syncWithDbAndSaveTimestamp(covidInfo))
    }

    private fun notifyUserAboutDistrictsUpdate(covidInfo: CovidInfoItem): Completable {
        return notifyDistrictsUpdatedUseCase.execute(
            covidInfo.voivodeships
                .map { it.districts }
                .flatten()
        )
    }

    private fun syncWithDbAndSaveTimestamp(covidInfo: CovidInfoItem): Completable {
        return syncWithDb(covidInfo.voivodeships)
            .andThen(saveTimeUpdateTimestamp(covidInfo.voivodeshipsUpdated))
    }

    private fun syncWithDb(voivodeships: List<VoivodeshipItem>): Completable {
        return covidInfoRepository.syncDistrictsRestrictionsWithDb(voivodeships)
    }

    private fun saveTimeUpdateTimestamp(timestamp: Long): Completable {
        return covidInfoRepository.saveVoivodeshipsUpdateTimestamp(timestamp)
    }
}
