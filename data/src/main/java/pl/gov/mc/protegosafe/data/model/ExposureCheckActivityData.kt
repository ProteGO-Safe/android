package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ExposureCheckActivityData(
    @SerializedName("id")
    val id: String,
    @SerializedName("riskLevel")
    val riskLevel: Int,
    @SerializedName("exposures")
    val exposures: Int,
    @SerializedName("timestamp")
    val timestamp: Long
)
