package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.NotificationRepository

class SaveNotificationDataUseCase(
    private val notificationRepository: NotificationRepository
) {
    fun execute(data: String) {
        notificationRepository.saveNotificationData(data)
    }
}