package pl.gov.mc.protegosafe.domain.model

interface FcmNotificationMapper {
    fun toPushNotificationItem(
        remoteMessageData: Map<String, String>,
        topic: String?
    ): PushNotificationItem?

    fun toUINotificationJson(pushNotificationItem: PushNotificationItem): String
}
