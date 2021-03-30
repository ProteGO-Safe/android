package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ENStatsResultData(
    @SerializedName("riskCheck")
    val enStats: ENStatsData?
)
