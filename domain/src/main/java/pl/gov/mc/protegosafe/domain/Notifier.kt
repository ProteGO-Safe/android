package pl.gov.mc.protegosafe.domain

import pl.gov.mc.protegosafe.domain.model.DistrictsUpdatedNotificationType
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem

interface Notifier {
    fun showSimpleNotificationWithData(title: String, content: String, data: String)
    fun getDistrictsUpdatedNotification(notificationType: DistrictsUpdatedNotificationType): PushNotificationItem
    fun showDistrictsUpdatedNotification(notificationItem: PushNotificationItem)
    fun showCovidStatsUpdatedNotification(notificationItem: PushNotificationItem, data: String)
    fun cancelNotificationById(notificationId: Int)
}
