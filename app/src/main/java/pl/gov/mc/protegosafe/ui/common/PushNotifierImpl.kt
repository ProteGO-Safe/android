package pl.gov.mc.protegosafe.ui.common

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.domain.PushNotifier
import pl.gov.mc.protegosafe.ui.MainActivity
import timber.log.Timber

class PushNotifierImpl(private val context: Context) : PushNotifier {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun showNotificationWithData(title: String, content: String, data: String) {
        val notification = createNotification(title, content, data)
        notificationManager?.notify(Consts.GENERAL_NOTIFICATION_PUSH_ID, notification)
            ?: Timber.d("Show notification failed")
    }

    private fun createNotification(title: String, content: String, data: String): Notification {
        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(Consts.GENERAL_NOTIFICATION_EXTRA_DATA, data)
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
            .build().apply {
                flags = NotificationCompat.FLAG_AUTO_CANCEL
            }
    }
}
