package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

class ServicesStatusData(
    @SerializedName("isBtSupported")
    val isBtSupported: Boolean,
    @SerializedName("isExposureNotificationEnabled")
    val isExposureNotificationEnabled: Boolean,
    @SerializedName("isBtOn")
    val isBtOn: Boolean,
    @SerializedName("isBtServiceOn")
    val isBtServiceOn: Boolean
)
