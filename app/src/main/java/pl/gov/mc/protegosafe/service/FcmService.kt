package pl.gov.mc.protegosafe.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import pl.gov.mc.protegosafe.domain.model.FcmNotificationMapper
import pl.gov.mc.protegosafe.domain.usecase.OnPushNotificationUseCase

class FcmService : FirebaseMessagingService() {

    private val onPushNotificationUseCase: OnPushNotificationUseCase by inject()
    private val fcmNotificationMapper: FcmNotificationMapper by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            handleNotification(remoteMessage)
        }
    }

    private fun handleNotification(remoteMessage: RemoteMessage) {
        fcmNotificationMapper.toPushNotificationItem(remoteMessage.data, remoteMessage.from)?.let {
            onPushNotificationUseCase.execute(it)
        }
    }
}
