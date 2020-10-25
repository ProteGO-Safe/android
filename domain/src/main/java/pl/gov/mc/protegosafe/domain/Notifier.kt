package pl.gov.mc.protegosafe.domain

import pl.gov.mc.protegosafe.domain.model.DistrictsUpdatedNotificationType

interface Notifier {
    fun showNotificationWithData(title: String, content: String, data: String)
    fun showDistrictsUpdatedNotification(notificationType: DistrictsUpdatedNotificationType)
}
