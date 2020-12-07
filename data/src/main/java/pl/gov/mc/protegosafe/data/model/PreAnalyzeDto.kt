package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PreAnalyzeDto(
    @PrimaryKey
    var token: String = "",
    var keysCount: Long = 0L
) : RealmObject()
