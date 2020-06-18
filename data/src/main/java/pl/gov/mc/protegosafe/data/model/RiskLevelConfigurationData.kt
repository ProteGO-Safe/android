package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class RiskLevelConfigurationData(
    @SerializedName("maxNoRiskScore")
    val maxNoRiskScore: Int,
    @SerializedName("maxLowRiskScore")
    val maxLowRiskScore: Int,
    @SerializedName("maxMiddleRiskScore")
    val maxMiddleRiskScore: Int
)
