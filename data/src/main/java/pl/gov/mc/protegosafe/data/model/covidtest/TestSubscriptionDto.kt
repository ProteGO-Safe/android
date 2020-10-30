package pl.gov.mc.protegosafe.data.model.covidtest

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TestSubscriptionDto(
    var status: Int = 0,
    var accessToken: String = "",
    var guid: String = "",
    var updated: Long = 0L,
    @PrimaryKey
    var id: Int = 1
) : RealmObject()
