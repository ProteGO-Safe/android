package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class CovidStatsResultData(
    @SerializedName("covidStats")
    val covidStats: CovidStatsData?
)
