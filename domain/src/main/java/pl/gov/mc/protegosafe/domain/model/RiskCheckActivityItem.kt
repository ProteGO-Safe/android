package pl.gov.mc.protegosafe.domain.model

import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import java.util.UUID

data class RiskCheckActivityItem(
    val id: String = UUID.randomUUID().toString(),
    val keys: Long,
    val exposures: Int,
    val timestamp: Long = getCurrentTimeInSeconds()
)
