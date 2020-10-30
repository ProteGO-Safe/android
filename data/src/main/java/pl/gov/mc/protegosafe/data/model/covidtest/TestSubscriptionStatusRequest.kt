package pl.gov.mc.protegosafe.data.model.covidtest

class TestSubscriptionStatusRequest(
    val accessToken: String,
    val safetynetToken: String,
    val testSubscriptionStatusRequestBody: TestSubscriptionStatusRequestBody
)
