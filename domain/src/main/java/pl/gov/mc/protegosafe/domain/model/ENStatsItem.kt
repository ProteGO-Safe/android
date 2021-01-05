package pl.gov.mc.protegosafe.domain.model

data class ENStatsItem(
    val lastRiskCheckTimestamp: Long,
    val todayKeysCount: Long,
    val last7daysKeysCount: Long,
    val totalKeysCount: Long
)
