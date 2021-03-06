package pl.gov.mc.protegosafe.ui.common

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.core.app.NotificationCompat
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.model.RouteData
import pl.gov.mc.protegosafe.domain.Notifier
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.DistrictRestrictionStateItem
import pl.gov.mc.protegosafe.domain.model.DistrictsUpdatedNotificationType
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.usecase.GetLocaleUseCase
import pl.gov.mc.protegosafe.receiver.UnsubscribeCovidStatsTopicBroadcastReceiver
import pl.gov.mc.protegosafe.ui.MainActivity
import timber.log.Timber
import java.util.Random

class NotifierImpl(
    context: Context,
    private val getLocaleUseCase: GetLocaleUseCase
) : Notifier {

    private val localizedContext by lazy {
        getLocalizedContext(context) ?: context
    }

    private fun getLocalizedContext(context: Context?): Context? {
        val locale = getLocaleUseCase.execute()
        val config = Configuration(context?.resources?.configuration).apply {
            setLocale(locale)
        }

        return context?.createConfigurationContext(config)
    }

    private val notificationManager by lazy {
        localizedContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun showSimpleNotificationWithData(title: String, content: String, data: String) {
        val notification = createNotificationBuilder(title, content, data).build()
        showNotification(notification)
    }

    override fun getDistrictsUpdatedNotification(
        notificationType: DistrictsUpdatedNotificationType
    ): PushNotificationItem {
        return when (notificationType) {
            is DistrictsUpdatedNotificationType.EmptySubscribedDistrictsList -> {
                PushNotificationItem(
                    title = localizedContext.getString(R.string.changes_in_districts_notification_title),
                    content = localizedContext.getString(R.string.changes_in_districts_info)
                )
            }
            is DistrictsUpdatedNotificationType.NoDistrictsUpdated -> {
                PushNotificationItem(
                    title = localizedContext.getString(R.string.changes_in_districts_notification_title),
                    content = localizedContext.getString(R.string.no_changes_in_subscribed_districts_info),
                )
            }
            is DistrictsUpdatedNotificationType.DistrictsUpdated -> {
                PushNotificationItem(
                    title = localizedContext.getString(R.string.changes_in_districts_notification_title),
                    content = prepareDistrictsUpdatedNotificationContent(notificationType.districts),
                )
            }
        }
    }

    private fun prepareDistrictsUpdatedNotificationContent(updatedDistricts: List<DistrictItem>): String {
        val notificationContentRes = if (updatedDistricts.size == 1) {
            R.string.changes_in_subscribed_district_info_with_params
        } else {
            R.string.changes_in_subscribed_districts_info_with_params
        }
        val updatedDistrictsInfo = StringBuilder()
        updatedDistricts.forEach {
            val updateDistrictInfo = localizedContext.getString(
                R.string.changed_district_info_with_params,
                it.name,
                getDistrictRestrictionZoneName(it.state)
            )
            updatedDistrictsInfo.append(" ").append(updateDistrictInfo).append(",")
        }
        return localizedContext.getString(
            notificationContentRes,
            updatedDistricts.size,
            updatedDistrictsInfo.dropLastWhile { it == ',' }
        )
    }

    private fun getDistrictRestrictionZoneName(
        districtRestrictionState: DistrictRestrictionStateItem
    ): String {
        return localizedContext.getString(
            when (districtRestrictionState) {
                DistrictRestrictionStateItem.NEUTRAL -> {
                    R.string.district_restriction_zone_neutral
                }
                DistrictRestrictionStateItem.YELLOW -> {
                    R.string.district_restriction_zone_yellow
                }
                DistrictRestrictionStateItem.RED -> {
                    R.string.district_restriction_zone_red
                }
            }
        )
    }

    override fun showDistrictsUpdatedNotification(
        notificationItem: PushNotificationItem
    ) {
        val routeJson = RouteData(ROUTE_NAME_DISTRICT_RESTRICTIONS_DEFAULT, mutableMapOf()).toJson()
        createNotificationBuilder(
            title = notificationItem.title,
            content = notificationItem.content,
            routeJson = routeJson
        ).build().let {
            showNotification(it)
        }
    }

    override fun showCovidStatsUpdatedNotification(
        notificationItem: PushNotificationItem,
        data: String
    ) {
        val notificationId = Random().nextInt()
        val routeJson = if (data.isNullOrBlank()) {
            RouteData(ROUTE_NAME_COVID_STATS_DEFAULT, mutableMapOf()).toJson()
        } else {
            data
        }

        val intent = Intent(
            localizedContext,
            UnsubscribeCovidStatsTopicBroadcastReceiver::class.java
        ).apply {
            action = Consts.ACTION_UNSUBSCRIBE_COVID_STATS_TOPIC
            putExtra(Consts.COVID_STATS_NOTIIFICATION_EXTRA_ID, notificationId)
        }

        createNotificationBuilder(
            title = notificationItem.title,
            content = notificationItem.content,
            routeJson = routeJson
        ).addAction(
            0,
            localizedContext.getString(R.string.do_not_show_again),
            PendingIntent.getBroadcast(localizedContext, 0, intent, 0)
        ).build().let {
            Timber.d("Show notification $it, with id = $notificationId")
            showNotification(it, notificationId)
        }
    }

    private fun createNotificationBuilder(
        title: String,
        content: String,
        routeJson: String? = null
    ): NotificationCompat.Builder {
        val notificationIntent = Intent(localizedContext, MainActivity::class.java).apply {
            routeJson?.let { putExtra(Consts.GENERAL_NOTIFICATION_EXTRA_DATA, routeJson) }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            localizedContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(localizedContext, Consts.GENERAL_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setAutoCancel(true)
    }

    private fun showNotification(
        notification: Notification,
        notificationId: Int = Random().nextInt()
    ) {
        notificationManager?.notify(notificationId, notification)
            ?: Timber.d("Show notification failed")
    }

    override fun cancelNotificationById(notificationId: Int) {
        notificationManager?.cancel(notificationId) ?: Timber.d("Notification can't be cancelled")
    }
}

private const val ROUTE_NAME_COVID_STATS_DEFAULT = "home"
private const val ROUTE_NAME_DISTRICT_RESTRICTIONS_DEFAULT = "currentRestrictions"
