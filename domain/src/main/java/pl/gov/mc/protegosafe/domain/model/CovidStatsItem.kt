package pl.gov.mc.protegosafe.domain.model

data class CovidStatsItem(
    val updated: Long,
    val newCases: Long?,
    val totalCases: Long?,
    val newDeaths: Long?,
    val totalDeaths: Long?,
    val newRecovered: Long?,
    val totalRecovered: Long?,
    val newVaccinations: Long?,
    val totalVaccinations: Long?,
    val newVaccinationsDose1: Long?,
    val totalVaccinationsDose1: Long?,
    val newVaccinationsDose2: Long?,
    val totalVaccinationsDose2: Long?
)
