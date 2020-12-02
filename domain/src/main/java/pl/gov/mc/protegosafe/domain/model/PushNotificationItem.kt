package pl.gov.mc.protegosafe.domain.model

data class PushNotificationItem(
    val title: String,
    val content: String,
    val status: String,
    val topic: PushNotificationTopic
)
