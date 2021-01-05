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
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class NotifyDistrictsUpdatedUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val notifyManager: Notifier,
    private val activitiesRepository: ActivitiesRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(newDistrictsData: List<DistrictItem>): Completable {
        return covidInfoRepository.getSortedSubscribedDistricts()
            .flatMapCompletable { subscribedDistricts ->
                if (subscribedDistricts.isEmpty()) {
                    handleUpdateStatus(
                        DistrictsUpdatedNotificationType.EmptySubscribedDistrictsList
                    )
                } else {
                    getUpdatedSubscribedDistricts(newDistrictsData.sortedBy { it.id })
                        .flatMapCompletable { updatedDistricts ->
                            handleChangesInSubscribedDistricts(updatedDistricts)
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

    private fun handleChangesInSubscribedDistricts(
        districts: List<DistrictItem>
    ): Completable {
        return handleUpdateStatus(
            if (districts.isEmpty()) {
                DistrictsUpdatedNotificationType.NoDistrictsUpdated
            } else {
                DistrictsUpdatedNotificationType.DistrictsUpdated(districts)
            }
        )
    }

    private fun handleUpdateStatus(
        districtsUpdatedNotificationType: DistrictsUpdatedNotificationType
    ): Completable {
        return prepareNotification(districtsUpdatedNotificationType)
            .flatMapCompletable {
                saveNotificationActivity(it)
                    .andThen(
                        Completable.defer {
                            showNotification(it)
                        }
                    )
            }
    }

    private fun prepareNotification(
        districtsUpdatedNotificationType: DistrictsUpdatedNotificationType
    ): Single<PushNotificationItem> {
        return Single.fromCallable {
            notifyManager.getDistrictsUpdatedNotification(districtsUpdatedNotificationType)
        }
    }

    private fun showNotification(
        pushNotificationItem: PushNotificationItem
    ): Completable {
        return Completable.fromAction {
            notifyManager.showDistrictsUpdatedNotification(pushNotificationItem)
        }
    }

    private fun saveNotificationActivity(pushNotificationItem: PushNotificationItem): Completable {
        return activitiesRepository.saveNotificationActivity(pushNotificationItem)
            .ignoreElement()
    }
}
