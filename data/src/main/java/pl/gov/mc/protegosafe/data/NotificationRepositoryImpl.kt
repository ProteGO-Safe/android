package pl.gov.mc.protegosafe.data

import pl.gov.mc.protegosafe.data.db.NotificationDataStore
import pl.gov.mc.protegosafe.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val notificationDataStore: NotificationDataStore
): NotificationRepository {

    override fun getLatestNotificationData(): String {
        return notificationDataStore.notifData
    }

    override fun saveNotificationData(notificationData: String) {
        notificationDataStore.notifData = notificationData
    }
}
