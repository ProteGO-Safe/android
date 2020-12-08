package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.Consts
import pl.gov.mc.protegosafe.data.db.AppLanguageDataStore
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.model.PushNotificationContentData
import pl.gov.mc.protegosafe.data.model.RouteData
import pl.gov.mc.protegosafe.domain.model.FcmNotificationMapper
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationTopic

class FcmNotificationMapperImpl(
    private val languageDataStore: AppLanguageDataStore
) : FcmNotificationMapper {

    override fun getPushNotificationItem(
        remoteMessageData: Map<String, String>,
    ): Single<PushNotificationItem> {
        return Single.fromCallable {
            remoteMessageData[FCM_DATA_NOTIFICATION_KEY]?.let {
                Gson().fromJson(it, PushNotificationContentData::class.java)
                    .toPushNotificationItem()
            }
        }
    }

    override fun getPushNotificationTopic(
        remoteMessageData: Map<String, String>
    ): Single<PushNotificationTopic> {
        return Single.fromCallable {
            when (remoteMessageData[Consts.PUSH_NOTIFICATION_TOPIC_EXTRA]) {
                "/topics/${BuildConfig.MAIN_TOPIC}" -> PushNotificationTopic.MAIN
                "/topics/${BuildConfig.DAILY_TOPIC}" -> PushNotificationTopic.DAILY
                else -> PushNotificationTopic.UNKNOWN
            }
        }
    }

    override fun getRouteJsonWithNotificationUUID(
        remoteMessageData: Map<String, String>,
        uuid: String
    ): Single<String> {
        return Single.fromCallable {
            val route = remoteMessageData[FCM_DATA_ROUTE_KEY]?.let {
                Gson().fromJson(it, RouteData::class.java)
                    ?.apply {
                        // Can be [null] because Gson breaks null safety
                        if (params != null) {
                            this.params[ROUTE_UUID_KEY] = uuid
                        } else {
                            params = mutableMapOf(Pair(ROUTE_UUID_KEY, uuid))
                        }
                    }
            } ?: RouteData(
                name = ROUTE_NAME_FCM_DEFAULT,
                params = mutableMapOf(Pair(ROUTE_UUID_KEY, uuid))
            )
            route.toJson()
        }
    }

    private fun PushNotificationContentData.toPushNotificationItem(): PushNotificationItem {
        return this.localizedNotifications.firstOrNull {
            languageDataStore.appLanguageISO.equals(it.languageISO, ignoreCase = true)
        }?.let {
            PushNotificationItem(
                title = it.title,
                content = it.content,
            )
        } ?: throw NullPointerException("No localized notification included")
    }
}

private const val FCM_DATA_NOTIFICATION_KEY = "notification"
private const val FCM_DATA_ROUTE_KEY = "route"
private const val ROUTE_UUID_KEY = "uuid"
private const val ROUTE_NAME_FCM_DEFAULT = "notificationsHistory"
