package se.sigmaconnectivity.blescanner.domain.model

data class PushNotificationData(
    val title: String,
    val content: String,
    val topic: PushNotificationTopic
)