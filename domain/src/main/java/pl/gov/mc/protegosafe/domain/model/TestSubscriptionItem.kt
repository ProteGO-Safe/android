package pl.gov.mc.protegosafe.domain.model

data class TestSubscriptionItem(
    val guid: String,
    val status: FreeCovidTestSubscriptionStatus,
    val accessToken: String,
    var updated: Long
)
