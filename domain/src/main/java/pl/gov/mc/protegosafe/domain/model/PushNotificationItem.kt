package pl.gov.mc.protegosafe.domain.model

import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import java.util.UUID

data class PushNotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val timestamp: Long = getCurrentTimeInSeconds()
)
