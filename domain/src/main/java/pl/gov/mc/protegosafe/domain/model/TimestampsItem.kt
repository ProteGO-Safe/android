package pl.gov.mc.protegosafe.domain.model

data class TimestampsItem(
    val nextUpdate: Long = 0,
    val dashboardUpdated: Long = 0,
    val detailsUpdated: Long = 0,
    val districtsUpdated: Long = 0
)
