package pl.gov.mc.protegosafe.data.mapper

import pl.gov.mc.protegosafe.domain.model.PushNotificationData
import pl.gov.mc.protegosafe.domain.model.PushNotificationTopic

private const val FCM_NOTIFICATION_TITLE_KEY = "title"
private const val FCM_NOTIFICATION_CONTENT_KEY = "content"
fun Map<String, String>.hasNotification() =
    !get(FCM_NOTIFICATION_TITLE_KEY).isNullOrBlank()

fun Map<String, String>.toNotificationDataItem(topic: String?) = PushNotificationData(
    title = get(FCM_NOTIFICATION_TITLE_KEY)
        ?: throw IllegalArgumentException("Hash id has no value"),
    content = get(FCM_NOTIFICATION_CONTENT_KEY) ?: "",
    topic = PushNotificationTopic.of(topic ?: "")
)