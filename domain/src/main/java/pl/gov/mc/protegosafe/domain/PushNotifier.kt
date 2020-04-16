package pl.gov.mc.protegosafe.domain

interface PushNotifier {
    fun showNotification(title: String, content: String)
}