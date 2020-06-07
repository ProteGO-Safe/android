package pl.gov.mc.protegosafe.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import pl.gov.mc.protegosafe.data.mapper.hasNotification
import pl.gov.mc.protegosafe.data.mapper.toNotificationDataItem
import pl.gov.mc.protegosafe.domain.usecase.OnPushNotificationUseCase

class FcmService : FirebaseMessagingService() {

    private val onPushNotificationUseCase: OnPushNotificationUseCase by inject()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
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
}
