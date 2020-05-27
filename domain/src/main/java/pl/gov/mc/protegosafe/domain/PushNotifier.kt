package pl.gov.mc.protegosafe.domain

interface PushNotifier {
    fun showNotificationWithData(title: String, content: String, data: String)
}
