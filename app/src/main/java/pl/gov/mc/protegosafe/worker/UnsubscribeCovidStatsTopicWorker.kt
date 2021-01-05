package pl.gov.mc.protegosafe.worker

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.domain.Notifier
import pl.gov.mc.protegosafe.domain.repository.AppRepository

class UnsubscribeCovidStatsTopicWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val appRepository: AppRepository by inject()
    private val notifier: Notifier by inject()

    override fun createWork(): Single<Result> {
        return dismissNotification(inputData.getInt(Consts.COVID_STATS_NOTIIFICATION_EXTRA_ID, 0))
            .andThen(
                Completable.defer {
                    unsubscribeFromTopic()
                }
            )
            .toSingleDefault(Result.success())
    }

    private fun unsubscribeFromTopic(): Completable? {
        return appRepository.setCovidStatsNotificationsAgreement(false)
            .andThen(
                Completable.defer {
                    appRepository.unsubscribeFromCovidStatsNotificationsTopic()
                }
            )
    }

    private fun dismissNotification(notificationId: Int): Completable {
        return Completable.fromAction {
            notifier.cancelNotificationById(notificationId)
        }
    }
}
