package pl.gov.mc.protegosafe.data.model.freecovidtest

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TestSubscriptionPinDto(
    @PrimaryKey
    var id: Int = 1,
    var testPin: String = ""
) : RealmObject()
