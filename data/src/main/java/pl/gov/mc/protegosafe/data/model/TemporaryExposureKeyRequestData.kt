package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class TemporaryExposureKeyRequestData(
    @SerializedName("key")
    val base64Key: String,
    @SerializedName("rollingStartNumber")
    val rollingStartNumber: Int,
    @SerializedName("rollingPeriod")
    val rollingPeriod: Int,
    @SerializedName("transmissionRisk")
    val transmissionRisk: Int
)
