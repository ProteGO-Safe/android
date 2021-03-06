package pl.gov.mc.protegosafe.worker

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.model.FcmNotificationMapper
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import pl.gov.mc.protegosafe.domain.usecase.ShowPushNotificationUseCase
import pl.gov.mc.protegosafe.domain.usecase.info.UpdateDashboardUseCase

class HandleFcmNotificationWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val showPushNotificationUseCase: ShowPushNotificationUseCase by inject()
    private val fcmNotificationMapper: FcmNotificationMapper by inject()
    private val activitiesRepository: ActivitiesRepository by inject()
    private val updateDashboardUseCase: UpdateDashboardUseCase by inject()

    private val notificationData by lazy {
        mutableMapOf<String, String>().apply {
            inputData.keyValueMap.map { entry ->
                (entry.value as? String)?.let {
                    this[entry.key] = it
                }
            }
        }.toMap()
    }

    override fun createWork(): Single<Result> {
        return fcmNotificationMapper.getPushNotificationItem(notificationData)
            .flatMapCompletable { notification ->
                activitiesRepository.saveNotificationActivity(notification)
                    .flatMapCompletable {
                        showNotification(notification, it)
                    }
            }.andThen(
                Completable.defer {
                    updateDashboardIfAvailable(notificationData)
                }
            )
            .toSingleDefault(Result.success())
    }

    private fun showNotification(
        notificationItem: PushNotificationItem,
        uuid: String
    ): Completable {
        return fcmNotificationMapper.getPushNotificationTopic(notificationData)
            .flatMapCompletable { topic ->
                fcmNotificationMapper.getRouteJsonWithNotificationUUID(notificationData, uuid)
                    .flatMapCompletable { route ->
                        Completable.fromAction {
                            showPushNotificationUseCase.execute(
                                topic = topic,
                                notificationItem = notificationItem,
                                data = route
                            )
                        }
                    }
            }
    }

    private fun updateDashboardIfAvailable(notificationData: Map<String, String>): Completable {
        return fcmNotificationMapper
            .getDashboard(notificationData)
            .flatMapCompletable(updateDashboardUseCase::execute)
    }
}
