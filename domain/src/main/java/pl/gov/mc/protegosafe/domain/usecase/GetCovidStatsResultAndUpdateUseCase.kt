package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.CovidStatsItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import java.util.concurrent.TimeUnit

class GetCovidStatsResultAndUpdateUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val outgoingBridgeDataResultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(onResultActionRequired: (ActionRequiredItem) -> Unit): Single<String> {
        return covidInfoRepository.getCovidStats()
            .flatMap { covidStats ->
                updateIfRequired(covidStats, onResultActionRequired)
                    .andThen(
                        getResult(covidStats)
                    )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(covidStatsItem: CovidStatsItem): Single<String> {
        return Single.fromCallable {
            outgoingBridgeDataResultComposer.composeCovidStatsResult(covidStatsItem)
        }
    }

    private fun updateIfRequired(
        covidStatsItem: CovidStatsItem,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return isUpdateRequired(covidStatsItem)
            .flatMapCompletable {
                if (it) {
                    sendUpdateRequest(onResultActionRequired)
                } else {
                    Completable.complete()
                }
            }
    }

    private fun isUpdateRequired(covidStatsItem: CovidStatsItem): Single<Boolean> {
        val currentTime = getCurrentTimeInSeconds()
        return covidInfoRepository.getCovidStatsCheckTimestamp()
            .map { lastCheck ->
                (
                    (currentTime - lastCheck) > MIN_TIME_FROM_CHECK_IN_SECONDS &&
                        (currentTime - covidStatsItem.updated) > MIN_TIME_FROM_UPDATE_IN_SECONDS
                    )
            }
    }

    private fun sendUpdateRequest(
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return Completable.fromAction {
            onResultActionRequired(ActionRequiredItem.UpdateCovidStats)
        }
    }

    companion object {
        private val MIN_TIME_FROM_UPDATE_IN_SECONDS = TimeUnit.HOURS.toSeconds(20L)
        private val MIN_TIME_FROM_CHECK_IN_SECONDS = TimeUnit.MINUTES.toSeconds(5L)
    }
}
