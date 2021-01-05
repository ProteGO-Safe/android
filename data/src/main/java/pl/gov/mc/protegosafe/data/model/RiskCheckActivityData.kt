package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class RiskCheckActivityData(
    @SerializedName("id")
    val id: String,
    @SerializedName("keys")
    val keys: Long,
    @SerializedName("exposures")
    val exposures: Int,
    @SerializedName("timestamp")
    val timestamp: Long
)
