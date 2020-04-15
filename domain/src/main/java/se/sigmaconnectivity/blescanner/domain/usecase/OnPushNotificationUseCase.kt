package se.sigmaconnectivity.blescanner.domain.usecase

import se.sigmaconnectivity.blescanner.domain.NotificationRepository
import se.sigmaconnectivity.blescanner.domain.PushNotifier

class OnPushNotificationUseCase(
    private val notificationRepository: NotificationRepository,
    private val pushNotifier: PushNotifier
) {

    fun execute(title: String, content: String, data: String) {
        notificationRepository.saveNotificationData(data)
        pushNotifier.showNotification(title, content)
    }
}