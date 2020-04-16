package se.sigmaconnectivity.blescanner.domain.usecase

import se.sigmaconnectivity.blescanner.domain.NotificationRepository
import se.sigmaconnectivity.blescanner.domain.PushNotifier
import se.sigmaconnectivity.blescanner.domain.TriageRepository
import se.sigmaconnectivity.blescanner.domain.model.PushNotificationData
import se.sigmaconnectivity.blescanner.domain.model.PushNotificationTopic
import java.util.*

class OnPushNotificationUseCase(
    private val notificationRepository: NotificationRepository,
    private val triageRepository: TriageRepository,
    private val pushNotifier: PushNotifier
) {

    fun execute(notificationData: PushNotificationData, data: String) {
        when (notificationData.topic) {
            PushNotificationTopic.GENERAL -> {
                saveDataAndShowNotification(notificationData, data)
            }
            PushNotificationTopic.DAILY -> {
                if (!checkIfToday(triageRepository.getLastTriageCompletedTimestamp())) {
                    saveDataAndShowNotification(notificationData, data)
                }
            }
            PushNotificationTopic.UNKNOWN -> {
                throw IllegalArgumentException("Unknown push topic type")
            }
        }
    }

    private fun saveDataAndShowNotification(notificationData: PushNotificationData, data: String) {
        notificationRepository.saveNotificationData(data)
        pushNotifier.showNotification(notificationData.title, notificationData.content)
    }

    private fun checkIfToday(timestamp: Long): Boolean {
        val now: Calendar = Calendar.getInstance()
        val timeToCheck: Calendar = Calendar.getInstance()
        timeToCheck.timeInMillis = timestamp
        return ((now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR))
                && (now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR)))
    }

}