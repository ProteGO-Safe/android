package pl.gov.mc.protegosafe.domain.model

data class PushNotificationData(
    val title: String,
    val content: String,
    val topic: PushNotificationTopic
)
