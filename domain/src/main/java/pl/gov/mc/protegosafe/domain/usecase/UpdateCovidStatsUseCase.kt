package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.CovidInfoItem
import pl.gov.mc.protegosafe.domain.model.CovidStatsItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateCovidStatsUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(covidInfo: CovidInfoItem? = null): Completable {
        return getCovidInfo(covidInfo)
            .flatMapCompletable {
                updateCovidStats(it.covidStatsItem)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    fun execute(covidStatsItem: CovidStatsItem): Completable {
        return updateCovidStats(covidStatsItem)
    }

    private fun getCovidInfo(covidInfo: CovidInfoItem?): Single<CovidInfoItem> {
        return if (covidInfo == null) {
            covidInfoRepository.getCovidInfo()
        } else {
            Single.just(covidInfo)
        }
    }

    private fun updateCovidStats(covidStats: CovidStatsItem): Completable {
        return covidInfoRepository.updateCovidStats(covidStats)
            .andThen(
                Completable.defer {
                    covidInfoRepository.saveCovidStatsCheckTimestamp(getCurrentTimeInSeconds())
                }
            )
    }
}
