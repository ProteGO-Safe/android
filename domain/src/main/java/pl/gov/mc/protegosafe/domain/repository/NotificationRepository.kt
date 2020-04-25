package pl.gov.mc.protegosafe.domain.repository

interface NotificationRepository {
    fun getLatestNotificationData(): String
    fun saveNotificationData(notificationData: String)
}