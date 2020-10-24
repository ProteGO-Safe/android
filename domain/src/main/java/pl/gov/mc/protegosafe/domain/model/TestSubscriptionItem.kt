package pl.gov.mc.protegosafe.domain.model

data class TestSubscriptionItem(
    val guid: String,
    val status: CovidTestSubscriptionStatus,
    val accessToken: String,
    var updated: Long
)
