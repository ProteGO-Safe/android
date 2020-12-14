package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CovidStatsDto(
    var updated: Long = 0,
    var newCases: Long = 0,
    var totalCases: Long = 0,
    var newDeaths: Long = 0,
    var totalDeaths: Long = 0,
    var newRecovered: Long = 0,
    var totalRecovered: Long = 0,
) : RealmObject() {
    @PrimaryKey
    var id: Int = 1
}
