package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class UINotificationData(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("status")
    val status: String
)
