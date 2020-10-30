package pl.gov.mc.protegosafe.domain.model

import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds

data class TestSubscriptionItem(
    val guid: String,
    val accessToken: String,
    val status: TestSubscriptionStatus = TestSubscriptionStatus.VERIFIED,
    val updated: Long = getCurrentTimeInSeconds()
)
