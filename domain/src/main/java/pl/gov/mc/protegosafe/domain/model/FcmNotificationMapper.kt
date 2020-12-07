package pl.gov.mc.protegosafe.domain.model

import io.reactivex.Single

interface FcmNotificationMapper {
    fun getPushNotificationItem(
        remoteMessageData: Map<String, String>
    ): Single<PushNotificationItem>
    fun getPushNotificationTopic(
        remoteMessageData: Map<String, String>
    ): Single<PushNotificationTopic>
    fun getRouteJsonWithNotificationUUID(
        remoteMessageData: Map<String, String>,
        uuid: String
    ): Single<String>
}
