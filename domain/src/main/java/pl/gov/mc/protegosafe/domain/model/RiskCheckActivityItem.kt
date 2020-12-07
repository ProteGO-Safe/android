package pl.gov.mc.protegosafe.domain.model

data class RiskCheckActivityItem(
    val id: String,
    val keys: Long,
    val exposures: Int,
    val timestamp: Long
)
