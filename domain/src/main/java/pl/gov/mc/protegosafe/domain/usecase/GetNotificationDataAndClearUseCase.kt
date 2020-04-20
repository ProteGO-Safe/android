package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.NotificationRepository

class GetNotificationDataAndClearUseCase(
    private val notificationRepository: NotificationRepository
) {

    fun execute(): String {
        val data = notificationRepository.getLatestNotificationData()
        notificationRepository.saveNotificationData("")
        return data
    }
}