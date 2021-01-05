package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import java.util.UUID

open class RiskCheckActivityDto(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var keys: Long = 0L,
    var exposures: Int = 0,
    var timestamp: Long = getCurrentTimeInSeconds()
) : RealmObject()
