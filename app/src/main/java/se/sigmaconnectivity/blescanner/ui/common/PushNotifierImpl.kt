package se.sigmaconnectivity.blescanner.ui.common

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import se.sigmaconnectivity.blescanner.Consts
import se.sigmaconnectivity.blescanner.R
import se.sigmaconnectivity.blescanner.domain.PushNotifier
import se.sigmaconnectivity.blescanner.ui.MainActivity
import timber.log.Timber

class PushNotifierImpl(private val context: Context): PushNotifier {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    override fun showNotification(title: String, content: String) {
        val notification = createNotification(title, content)
        notificationManager?.let {
            it.notify(Consts.NOTIFICATION_PUSH_ID, notification)
            Timber.d("Show notification: $title, $content")
        } ?: Timber.d("Show notification failed")
    }

    private fun createNotification(title: String, content: String): Notification {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(context, Consts.NOTIFICATION_CHANNEL_ID)
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