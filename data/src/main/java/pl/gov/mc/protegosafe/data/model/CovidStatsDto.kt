package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CovidStatsDto(
    var updated: Long = 0,
    var newCases: Long? = 0,
    var totalCases: Long? = 0,
    var newDeaths: Long? = 0,
    var totalDeaths: Long? = 0,
    var newRecovered: Long? = 0,
    var totalRecovered: Long? = 0,
    var newVaccinations: Long? = null,
    var totalVaccinations: Long? = null,
    var newVaccinationsDose1: Long? = null,
    var totalVaccinationsDose1: Long? = null,
    var newVaccinationsDose2: Long? = null,
    var totalVaccinationsDose2: Long? = null
) : RealmObject() {
    @PrimaryKey
    var id: Int = 1
}
