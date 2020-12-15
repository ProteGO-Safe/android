package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.repository.AppRepository

class UpdateCovidStatsNotificationsStatusUseCase(
    private val appRepository: AppRepository,
    private val outgoingBridgePayloadMapper: OutgoingBridgePayloadMapper,
    private val getCovidStatsNotificationStatusUseCase: GetCovidStatsNotificationStatusUseCase,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(payload: String): Single<String> {
        return areNotificationsAllowed(payload)
            .flatMapCompletable {
                appRepository.setCovidStatsNotificationsAgreement(it)
                    .andThen(
                        handleNewSubscriptionState(it)
                    )
            }.andThen(
                getCovidStatsNotificationStatusUseCase.execute()
            )
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun areNotificationsAllowed(payload: String): Single<Boolean> {
        return Single.fromCallable {
            outgoingBridgePayloadMapper.areCovidStatsNotifcationsAllowed(payload)
        }
    }

    private fun handleNewSubscriptionState(areNotificationAllowed: Boolean): Completable {
        return if (areNotificationAllowed) {
            appRepository.subscribeToCovidStatsNotificationsTopic()
        } else {
            appRepository.unsubscribeFromCovidStatsNotificationsTopic()
        }
    }
}
