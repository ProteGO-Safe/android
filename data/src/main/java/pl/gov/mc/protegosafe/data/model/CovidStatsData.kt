package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class CovidStatsData(
    @SerializedName("updated")
    val updated: Long,
    @SerializedName("newCases")
    val newCases: Long?,
    @SerializedName("totalCases")
    val totalCases: Long?,
    @SerializedName("newDeaths")
    val newDeaths: Long?,
    @SerializedName("totalDeaths")
    val totalDeaths: Long?,
    @SerializedName("newRecovered")
    val newRecovered: Long?,
    @SerializedName("totalRecovered")
    val totalRecovered: Long?,
)
