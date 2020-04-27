package pl.gov.mc.protegosafe.model

import com.google.gson.annotations.SerializedName

data class ServicesStatusRoot (
    @SerializedName("servicesStatus")
    val servicesStatus: ServicesStatus
)

data class ServicesStatus (
    @SerializedName("isBtSupported")
    val isBtSupported: Boolean,
    @SerializedName("isLocationEnabled")
    val isLocationEnabled: Boolean,
    @SerializedName("isBtOn")
    val isBtOn: Boolean,
    @SerializedName("isBatteryOptimizationOn")
    val isBatteryOptimizationOn: Boolean,
    @SerializedName("isNotificationEnabled")
    val isNotificationEnabled: Boolean,
    @SerializedName("isBtServiceOn")
    val isBtServiceOn: Boolean
)