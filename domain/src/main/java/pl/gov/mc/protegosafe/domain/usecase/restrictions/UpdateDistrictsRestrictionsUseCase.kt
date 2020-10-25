package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.CovidInfoItem
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateDistrictsRestrictionsUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val notifyDistrictsUpdatedUseCase: NotifyDistrictsUpdatedUseCase,
    private val postExecutionThread: PostExecutionThread
) {

    private sealed class UpdateState {
        object NoNewData : UpdateState()
        class InitialData(val covidInfo: CovidInfoItem) : UpdateState()
        class NewData(val covidInfo: CovidInfoItem) : UpdateState()
    }

    fun execute(): Completable {
        return updateData()
            .flatMapCompletable { updateState ->
                when (updateState) {
                    is UpdateState.NoNewData -> {
                        Completable.complete()
                    }
                    is UpdateState.InitialData -> {
                        handleInitialData(updateState.covidInfo)
                    }
                    is UpdateState.NewData -> {
                        handleNewData(updateState.covidInfo)
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateData(): Single<UpdateState> {
        return covidInfoRepository.getCovidInfo()
            .zipWith<CovidInfoItem, Long, UpdateState>(
                covidInfoRepository.getCovidInfoUpdateTimestamp()
            ) { covidInfo: CovidInfoItem, updatedTimeStamp: Long ->
                when {
                    covidInfo.lastUpdate == updatedTimeStamp -> {
                        UpdateState.NoNewData
                    }
                    updatedTimeStamp == 0L -> {
                        UpdateState.InitialData(covidInfo)
                    }
                    else -> {
                        UpdateState.NewData(covidInfo)
                    }
                }
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
            .andThen(saveTimeUpdateTimestamp(covidInfo.lastUpdate))
    }

    private fun syncWithDb(voivodeships: List<VoivodeshipItem>): Completable {
        return covidInfoRepository.syncDistrictsRestrictionsWithDb(voivodeships)
    }

    private fun saveTimeUpdateTimestamp(timestamp: Long): Completable {
        return covidInfoRepository.saveCovidInfoUpdateTimestamp(timestamp)
    }
}
