package pl.gov.mc.protegosafe.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import pl.gov.mc.protegosafe.data.mapper.hasNotification
import pl.gov.mc.protegosafe.data.mapper.toNotificationDataItem
import pl.gov.mc.protegosafe.domain.usecase.OnPushNotificationUseCase
import timber.log.Timber

class FcmService : FirebaseMessagingService() {

    private val onPushNotificationUseCase: OnPushNotificationUseCase by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // TODO(developer): Handle FCM messages here.
        Timber.d("FCM from: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            handleNotification(remoteMessage)
        }
    }

    private fun handleNotification(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.hasNotification()) {
            remoteMessage.data.toNotificationDataItem(remoteMessage.from).let {
                onPushNotificationUseCase.execute(it, Gson().toJson(remoteMessage.data))
            }
        }
    }

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }
}