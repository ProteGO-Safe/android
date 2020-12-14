package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds

open class TotalKeysCountDto(
    var lastRiskCheckTimestamp: Long = getCurrentTimeInSeconds(),
    var totalKeysCount: Long = 0
) : RealmObject() {
    @PrimaryKey
    var id: Int = 1
}
