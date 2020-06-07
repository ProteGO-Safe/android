package pl.gov.mc.protegosafe.model

import com.google.gson.annotations.SerializedName

data class ServicesStatusRoot(
    @SerializedName("servicesStatus")
    val servicesStatus: ServicesStatus
)

data class ServicesStatus(
    @SerializedName("exposureNotificationStatus")
    val exposureNotificationStatus: Int,
    @SerializedName("isLocationOn")
    val isLocationOn: Boolean,
    @SerializedName("isBtOn")
    val isBtOn: Boolean,
    @SerializedName("isNotificationEnabled")
    val isNotificationEnabled: Boolean
)
