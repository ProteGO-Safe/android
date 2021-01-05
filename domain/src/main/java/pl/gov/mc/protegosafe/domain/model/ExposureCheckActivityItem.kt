package pl.gov.mc.protegosafe.domain.model

import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import java.util.UUID

data class ExposureCheckActivityItem(
    val id: String = UUID.randomUUID().toString(),
    val riskLevel: RiskLevelItem,
    val exposures: Int,
    val timestamp: Long = getCurrentTimeInSeconds()
)
