package pl.gov.mc.protegosafe.mapper

import io.bluetrace.opentrace.idmanager.TemporaryID
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem

fun TemporaryIDItem.toDeviceModel() = TemporaryID(
    startTime = startTime,
    tempID = tempID,
    expiryTime = expiryTime
)