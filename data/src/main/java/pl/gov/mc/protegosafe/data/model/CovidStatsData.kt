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
    @SerializedName("newVaccinations")
    val newVaccinations: Long?,
    @SerializedName("totalVaccinations")
    val totalVaccinations: Long?,
    @SerializedName("newVaccinationsDose1")
    val newVaccinationsDose1: Long?,
    @SerializedName("totalVaccinationsDose1")
    val totalVaccinationsDose1: Long?,
    @SerializedName("newVaccinationsDose2")
    val newVaccinationsDose2: Long?,
    @SerializedName("totalVaccinationsDose2")
    val totalVaccinationsDose2: Long?
)
