package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.CovidInfoItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateCovidStatsUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(covidInfo: CovidInfoItem? = null): Completable {
        return getCovidInfo(covidInfo)
            .flatMapCompletable {
                updateCovidStats(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getCovidInfo(covidInfo: CovidInfoItem?): Single<CovidInfoItem> {
        return if (covidInfo == null) {
            covidInfoRepository.getCovidInfo()
        } else {
            Single.just(covidInfo)
        }
    }

    private fun updateCovidStats(covidInfo: CovidInfoItem): Completable {
        return covidInfoRepository.updateCovidStats(covidInfo.covidStatsItem)
            .andThen(
                Completable.defer {
                    covidInfoRepository.saveCovidStatsCheckTimestamp(getCurrentTimeInSeconds())
                }
            )
    }
}
