package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.AppRepository

class SubscribeCovidStatusTopicUseCase(
    private val appRepository: AppRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Completable {
        return appRepository.areCovidStatsNotificationsAllowed()
            .flatMapCompletable {
                if (it) {
                    appRepository.subscribeToCovidStatsNotification()
                } else {
                    throw IllegalStateException("Covid stats not allowed")
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
