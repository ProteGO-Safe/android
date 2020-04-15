package se.sigmaconnectivity.blescanner.domain

interface NotificationRepository {
    fun getLatestNotificationData(): String
    fun saveNotificationData(notificationData: String)
}