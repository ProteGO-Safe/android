package se.sigmaconnectivity.blescanner.domain.usecase

import se.sigmaconnectivity.blescanner.domain.NotificationRepository

class GetNotificationDataAndClearUseCase(
    private val notificationRepository: NotificationRepository
) {

    fun execute(): String {
        val data = notificationRepository.getLatestNotificationData()
        notificationRepository.saveNotificationData("")
        return data
    }
}