package se.sigmaconnectivity.blescanner.domain

interface PushNotifier {
    fun showNotification(title: String, content: String)
}