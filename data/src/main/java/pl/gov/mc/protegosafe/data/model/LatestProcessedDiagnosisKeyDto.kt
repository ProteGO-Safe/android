package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LatestProcessedDiagnosisKeyDto(
    @PrimaryKey
    var id: Int = 1,
    var timestamp: Long = 0
) : RealmObject()
