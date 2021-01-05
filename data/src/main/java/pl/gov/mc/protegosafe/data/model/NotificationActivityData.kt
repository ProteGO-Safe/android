package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class NotificationActivityData(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("content")
    var content: String,
    @SerializedName("timestamp")
    var timestamp: Long
)
