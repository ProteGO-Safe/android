package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.AppRepository

class GetCovidStatsNotificationStatusUseCase(
    private val appRepository: AppRepository,
    private val outgoingBridgeDataResultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return appRepository.areCovidStatsNotificationsAllowed()
            .flatMap {
                getResult(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(areAllowed: Boolean): Single<String> {
        return Single.fromCallable {
            outgoingBridgeDataResultComposer.composeCovidStatsNotificationsStatusResult(areAllowed)
        }
    }
}
