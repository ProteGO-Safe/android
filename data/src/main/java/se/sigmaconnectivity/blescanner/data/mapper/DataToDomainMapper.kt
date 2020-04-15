package se.sigmaconnectivity.blescanner.data.mapper

import se.sigmaconnectivity.blescanner.domain.model.PushNotificationData

private const val FCM_NOTIFICATION_TITLE_KEY = "title"
private const val FCM_NOTIFICATION_CONTENT_KEY = "content"
fun Map<String, String>.hasNotification() =
    !get(FCM_NOTIFICATION_TITLE_KEY).isNullOrBlank()

fun Map<String, String>.toNotificationDataItem() = PushNotificationData(
    title = get(FCM_NOTIFICATION_TITLE_KEY)
        ?: throw IllegalArgumentException("Hash id has no value"),
    content = get(FCM_NOTIFICATION_CONTENT_KEY) ?: ""
)