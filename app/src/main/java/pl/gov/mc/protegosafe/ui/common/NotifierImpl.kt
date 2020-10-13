package pl.gov.mc.protegosafe.ui.common

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.domain.Notifier
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.DistrictsUpdatedNotificationType
import pl.gov.mc.protegosafe.ui.MainActivity
import timber.log.Timber
import java.util.Random

class NotifierImpl(private val context: Context) : Notifier {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun showNotificationWithData(title: String, content: String, data: String) {
        val notification = createNotification(title, content, data)
        notificationManager?.notify(Consts.GENERAL_NOTIFICATION_PUSH_ID, notification)
            ?: Timber.d("Show notification failed")
    }

    override fun showDistrictsUpdatedNotification(notificationTypeType: DistrictsUpdatedNotificationType) {
        notificationManager?.notify(
            Random().nextInt(),
            when (notificationTypeType) {
                is DistrictsUpdatedNotificationType.EmptySubscribedDistrictsList -> {
                    createNotification(
                        context.getString(R.string.changes_in_districts),
                        context.getString(R.string.changes_in_districts_info),
                    )
                }
                is DistrictsUpdatedNotificationType.NoDistrictsUpdated -> {
                    createNotification(
                        context.getString(R.string.no_changes_in_subscribed_districts),
                        context.getString(R.string.no_changes_in_subscribed_districts_info)
                    )
                }
                is DistrictsUpdatedNotificationType.DistrictsUpdated -> {
                    createNotification(
                        context.getString(R.string.changes_in_districts),
                        prepareDistrictsUpdatedNotificationContent(notificationTypeType.districts)
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
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            data?.let { putExtra(Consts.GENERAL_NOTIFICATION_EXTRA_DATA, data) }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(context, Consts.GENERAL_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .build().apply {
                flags = NotificationCompat.FLAG_AUTO_CANCEL
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
            val updateDistrictInfo = context.getString(
                R.string.changed_district_info_with_params,
                it.name,
                context.resources
                    .getStringArray(R.array.district_restriction_colors)[it.state.value]
            )
            updatedDistrictsInfo.append(" ").append(updateDistrictInfo).append(",")
        }
        return context.getString(
            notificationContentRes,
            updatedDistricts.size.toString(),
            updatedDistrictsInfo.dropLastWhile { it == ',' }
        )
    }
}
