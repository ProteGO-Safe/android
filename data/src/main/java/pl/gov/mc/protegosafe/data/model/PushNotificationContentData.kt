package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class PushNotificationContentData(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("localizedNotifications")
    val localizedNotifications: List<PushNotificationLocalizedContentData>
)
