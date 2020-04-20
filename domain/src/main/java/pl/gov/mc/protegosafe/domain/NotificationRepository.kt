package pl.gov.mc.protegosafe.domain

interface NotificationRepository {
    fun getLatestNotificationData(): String
    fun saveNotificationData(notificationData: String)
}