package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class PushNotificationContentData(
    @SerializedName("localizedNotifications")
    val localizedNotifications: List<PushNotificationLocalizedContentData>
)
