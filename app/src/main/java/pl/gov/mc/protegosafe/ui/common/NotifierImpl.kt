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
import pl.gov.mc.protegosafe.domain.Notifier
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.DistrictRestrictionStateItem
import pl.gov.mc.protegosafe.domain.model.DistrictsUpdatedNotificationType
import pl.gov.mc.protegosafe.domain.usecase.GetLocaleUseCase
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

    override fun showNotificationWithData(title: String, content: String, data: String) {
        val notification = createNotification(title, content, data)
        notificationManager?.notify(Consts.GENERAL_NOTIFICATION_PUSH_ID, notification)
            ?: Timber.d("Show notification failed")
    }

    override fun showDistrictsUpdatedNotification(notificationType: DistrictsUpdatedNotificationType) {
        notificationManager?.notify(
            Random().nextInt(),
            when (notificationType) {
                is DistrictsUpdatedNotificationType.EmptySubscribedDistrictsList -> {
                    createNotification(
                        localizedContext.getString(R.string.changes_in_districts_notification_title),
                        localizedContext.getString(R.string.changes_in_districts_info),
                    )
                }
                is DistrictsUpdatedNotificationType.NoDistrictsUpdated -> {
                    createNotification(
                        localizedContext.getString(R.string.changes_in_districts_notification_title),
                        localizedContext.getString(R.string.no_changes_in_subscribed_districts_info)
                    )
                }
                is DistrictsUpdatedNotificationType.DistrictsUpdated -> {
                    createNotification(
                        localizedContext.getString(R.string.changes_in_districts_notification_title),
                        prepareDistrictsUpdatedNotificationContent(notificationType.districts)
                    )
                }
            }
        ) ?: Timber.d("Show notification failed")
    }

    private fun createNotification(
        title: String,
        content: String,
        data: String? = null
    ): Notification {
        val notificationIntent = Intent(localizedContext, MainActivity::class.java).apply {
            data?.let { putExtra(Consts.GENERAL_NOTIFICATION_EXTRA_DATA, data) }
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
            .build()
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
}
