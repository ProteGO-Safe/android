package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.Notifier
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.DistrictsUpdatedNotificationType
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class NotifyDistrictsUpdatedUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val notifyManager: Notifier,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(newDistrictsData: List<DistrictItem>): Completable {
        return covidInfoRepository.getSortedSubscribedDistricts()
            .flatMapCompletable { subscribedDistricts ->
                if (subscribedDistricts.isEmpty()) {
                    showDistrictsUpdatedNotification(
                        DistrictsUpdatedNotificationType.EmptySubscribedDistrictsList
                    )
                } else {
                    getUpdatedSubscribedDistricts(newDistrictsData.sortedBy { it.id })
                        .flatMapCompletable { updatedDistricts ->
                            showChangesInSubscribedDistrictsNotification(updatedDistricts)
                        }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getUpdatedSubscribedDistricts(
        newDistrictsStatuses: List<DistrictItem>
    ): Single<List<DistrictItem>> {
        return covidInfoRepository.getSortedSubscribedDistricts()
            .flatMapObservable {
                it.toObservable()
            }
            .flatMapMaybe { subscribedDistrict ->
                getDistrictIfStatusUpdated(
                    subscribedDistrict,
                    newDistrictsStatuses[subscribedDistrict.id - 1]
                )
            }
            .toList()
    }

    private fun getDistrictIfStatusUpdated(
        subscribedDistrict: DistrictItem,
        newDistrict: DistrictItem
    ): Maybe<DistrictItem> {
        return if (subscribedDistrict.state != newDistrict.state) {
            Maybe.just(newDistrict)
        } else {
            Maybe.empty()
        }
    }

    private fun showChangesInSubscribedDistrictsNotification(
        districts: List<DistrictItem>
    ): Completable {
        return showDistrictsUpdatedNotification(
            if (districts.isEmpty()) {
                DistrictsUpdatedNotificationType.NoDistrictsUpdated
            } else {
                DistrictsUpdatedNotificationType.DistrictsUpdated(districts)
            }
        )
    }

    private fun showDistrictsUpdatedNotification(
        districtsUpdatedNotificationType: DistrictsUpdatedNotificationType
    ): Completable {
        return Completable.fromAction {
            notifyManager.showDistrictsUpdatedNotification(districtsUpdatedNotificationType)
        }
    }
}
