package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

class ServicesStatusData (
    @SerializedName("isBtSupported")
    val isBtSupported: Boolean,
    @SerializedName("isLocationEnabled")
    val isLocationEnabled: Boolean,
    @SerializedName("isBtOn")
    val isBtOn: Boolean,
    @SerializedName("isBatteryOptimizationOn")
    val isBatteryOptimizationOn: Boolean,
    @SerializedName("isBtServiceOn")
    val isBtServiceOn: Boolean
)