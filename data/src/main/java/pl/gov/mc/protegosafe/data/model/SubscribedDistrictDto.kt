package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SubscribedDistrictDto(
    @PrimaryKey
    var id: Int = 0,
    var updated: Long = System.currentTimeMillis() / 1000
) : RealmObject()
