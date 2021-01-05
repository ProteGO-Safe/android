package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class CovidInfoResponseData(
    @SerializedName("voivodeshipsUpdated")
    val voivodeshipsUpdated: Long,
    @SerializedName("voivodeships")
    val voivodeships: List<VoivodeshipData>,
    @SerializedName("covidStats")
    val covidStats: CovidStatsData
)
