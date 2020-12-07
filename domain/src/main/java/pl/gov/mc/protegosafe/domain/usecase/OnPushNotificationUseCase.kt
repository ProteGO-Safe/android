package pl.gov.mc.protegosafe.domain.usecase

import java.util.Calendar
import pl.gov.mc.protegosafe.domain.Notifier
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationTopic
import pl.gov.mc.protegosafe.domain.repository.TriageRepository

class OnPushNotificationUseCase(
    private val triageRepository: TriageRepository,
    private val notifier: Notifier
) {

    fun execute(
        topic: PushNotificationTopic,
        notificationItem: PushNotificationItem,
        data: String
    ) {
        when (topic) {
            PushNotificationTopic.DAILY -> {
                if (!checkIfToday(triageRepository.getLastTriageCompletedTimestamp())) {
                    showNotificationWithData(notificationItem, data)
                }
            }
            else -> {
                showNotificationWithData(notificationItem, data)
            }
        }
    }

    private fun showNotificationWithData(notificationItem: PushNotificationItem, data: String) {
        notifier.showNotificationWithData(notificationItem.title, notificationItem.content, data)
    }

    private fun checkIfToday(timestamp: Long): Boolean {
        val now: Calendar = Calendar.getInstance()
        val timeToCheck: Calendar = Calendar.getInstance()
        timeToCheck.timeInMillis = timestamp * 1000
        return (
            (now.get(Calendar.YEAR) == timeToCheck.get(Calendar.YEAR)) &&
                (now.get(Calendar.DAY_OF_YEAR) == timeToCheck.get(Calendar.DAY_OF_YEAR))
            )
    }
}
