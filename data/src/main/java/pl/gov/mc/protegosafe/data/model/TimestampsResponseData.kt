package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class TimestampsResponseData(
    @SerializedName("nextUpdate") val nextUpdate: Long,
    @SerializedName("dashboardUpdated") val dashboardUpdated: Long,
    @SerializedName("detailsUpdated") val detailsUpdated: Long,
    @SerializedName("districtsUpdated") val districtsUpdated: Long
)
