package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.PushNotifier
import pl.gov.mc.protegosafe.domain.TriageRepository
import pl.gov.mc.protegosafe.domain.model.PushNotificationData
import pl.gov.mc.protegosafe.domain.model.PushNotificationTopic
import java.util.*

class OnPushNotificationUseCase(
    private val triageRepository: TriageRepository,
    private val pushNotifier: PushNotifier
) {

    fun execute(notificationData: PushNotificationData, data: String) {
        when (notificationData.topic) {
            PushNotificationTopic.MAIN -> {
                showNotificationWithData(notificationData, data)
            }
            PushNotificationTopic.DAILY -> {
                if (!checkIfToday(triageRepository.getLastTriageCompletedTimestamp())) {
                    showNotificationWithData(notificationData, data)
                }
            }
            PushNotificationTopic.UNKNOWN -> {
                throw IllegalArgumentException("Unknown push topic type")
            }
        }
    }

    private fun showNotificationWithData(notificationData: PushNotificationData, data: String) {
        pushNotifier.showNotificationWithData(notificationData.title, notificationData.content, data)
    }

    private fun checkIfToday(timestamp: Long): Boolean {
        val now: Calendar = Calendar.getInstance()
        val timeToCheck: Calendar = Calendar.getInstance()
        timeToCheck.timeInMillis = timestamp * 1000
        return ((now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR))
                && (now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR)))
    }

}