package pl.gov.mc.protegosafe.domain.model

import java.util.concurrent.TimeUnit

data class TestSubscriptionItem(
    val guid: String,
    val accessToken: String,
    val status: TestSubscriptionStatus = TestSubscriptionStatus.VERIFIED,
    val updated: Long = System.currentTimeMillis() / TimeUnit.SECONDS.toMillis(1),
)
