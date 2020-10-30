package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds

open class SubscribedDistrictDto(
    @PrimaryKey
    var id: Int = 0,
    var updated: Long = getCurrentTimeInSeconds()
) : RealmObject()
