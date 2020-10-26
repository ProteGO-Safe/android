package pl.gov.mc.protegosafe.data.model.covidtest

import com.google.gson.annotations.SerializedName

data class TestSubscriptionStatusResult(
    @SerializedName("subscription")
    val subscription: TestSubscriptionStatusData?
)
