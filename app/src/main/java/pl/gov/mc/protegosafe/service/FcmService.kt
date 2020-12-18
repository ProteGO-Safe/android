package pl.gov.mc.protegosafe.service

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import pl.gov.mc.protegosafe.data.Consts
import pl.gov.mc.protegosafe.worker.HandleFcmNotificationWorker

class FcmService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            handleNotification(remoteMessage)
        }
    }

    private fun handleNotification(remoteMessage: RemoteMessage) {
        val dataBuilder = Data.Builder().apply {
            remoteMessage.data.entries.forEach { entry ->
                putString(
                    entry.key, entry.value
                )
            }
            putString(Consts.PUSH_NOTIFICATION_TOPIC_EXTRA, remoteMessage.from)
        }

        val workManager: WorkManager by inject()
        workManager.enqueue(
            OneTimeWorkRequest.Builder(HandleFcmNotificationWorker::class.java)
                .setInputData(dataBuilder.build())
                .build()
        )
    }
}
