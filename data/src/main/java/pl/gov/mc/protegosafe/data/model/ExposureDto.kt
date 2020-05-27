package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.UUID

open class ExposureDto(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var date: Long = 0,
    var durationInMinutes: Int = 0,
    var riskScore: Int = 0,
    var lastUpdatedTimestampMs: Long = System.currentTimeMillis()
) : RealmObject()
