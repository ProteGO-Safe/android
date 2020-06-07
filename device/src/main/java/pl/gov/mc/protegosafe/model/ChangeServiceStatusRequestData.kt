package pl.gov.mc.protegosafe.model

import com.google.gson.annotations.SerializedName

data class ChangeServiceStatusRequestData(
    @SerializedName("enableExposureNotificationService")
    val enableExposureNotificationService: Boolean?,
    @SerializedName("enableBt")
    val enableBt: Boolean?,
    @SerializedName("enableLocation")
    val enableLocation: Boolean?,
    @SerializedName("enableNotification")
    val enableNotification: Boolean?
)
