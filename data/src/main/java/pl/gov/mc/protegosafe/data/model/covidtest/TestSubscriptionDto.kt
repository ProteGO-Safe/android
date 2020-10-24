package pl.gov.mc.protegosafe.data.model.covidtest

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.UUID

open class TestSubscriptionDto(
    @PrimaryKey
    var id: Int = 1,
    var status: Int = 0,
    var accessToken: String = "",
    var guid: String = UUID.randomUUID().toString(),
    var updated: Long = System.currentTimeMillis()
) : RealmObject()
