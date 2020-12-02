package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.db.AppLanguageDataStore
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.model.PushNotificationContentData
import pl.gov.mc.protegosafe.domain.model.FcmNotificationMapper
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationTopic
import timber.log.Timber

class FcmNotificationMapperImpl(
    private val languageDataStore: AppLanguageDataStore
) : FcmNotificationMapper {

    override fun toPushNotificationItem(
        remoteMessageData: Map<String, String>,
        topic: String?
    ): PushNotificationItem? {
        try {
            remoteMessageData[FCM_DATA_NOTIFICATION_KEY]?.let {
                return Gson().fromJson(it, PushNotificationContentData::class.java)
                    .toPushNotificationItem(topic)
            }
        } catch (e: Exception) {
            Timber.e(e.message, "Notification can't be parsed")
        }
        return null
    }

    private fun PushNotificationContentData.toPushNotificationItem(from: String?): PushNotificationItem {
        val topic = when (from) {
            "/topics/${BuildConfig.MAIN_TOPIC}" -> PushNotificationTopic.MAIN
            "/topics/${BuildConfig.DAILY_TOPIC}" -> PushNotificationTopic.DAILY
            else -> PushNotificationTopic.UNKNOWN
        }
        val localizedNotification = this.localizedNotifications.firstOrNull {
            languageDataStore.appLanguageISO.equals(it.languageISO, ignoreCase = true)
        }
        return PushNotificationItem(
            title = localizedNotification?.title ?: this.title,
            content = localizedNotification?.content ?: this.content,
            status = this.status,
            topic = topic
        )
    }

    override fun toUINotificationJson(pushNotificationItem: PushNotificationItem): String {
        return pushNotificationItem.toUINotificationData().toJson()
    }
}

private const val FCM_DATA_NOTIFICATION_KEY = "notification"
