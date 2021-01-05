package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class PushNotificationLocalizedContentData(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("languageISO")
    val languageISO: String
)
