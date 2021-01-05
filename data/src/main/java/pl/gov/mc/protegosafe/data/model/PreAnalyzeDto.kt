package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds

open class PreAnalyzeDto(
    @PrimaryKey
    var token: String = "",
    var keysCount: Long = 0L,
    var timestamp: Long = getCurrentTimeInSeconds()
) : RealmObject()
