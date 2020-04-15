package se.sigmaconnectivity.blescanner.data

import se.sigmaconnectivity.blescanner.data.db.NotificationDataStore
import se.sigmaconnectivity.blescanner.domain.NotificationRepository

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