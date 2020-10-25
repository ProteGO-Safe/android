package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DistrictDto(
    @PrimaryKey
    var id: Int = 0,
    var name: String = "",
    var state: Int = 0
) : RealmObject()
