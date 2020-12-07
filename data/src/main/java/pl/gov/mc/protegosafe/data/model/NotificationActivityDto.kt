package pl.gov.mc.protegosafe.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import java.util.UUID

open class NotificationActivityDto(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var content: String = "",
    var timestamp: Long = getCurrentTimeInSeconds()
) : RealmObject()
