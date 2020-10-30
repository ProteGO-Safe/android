package pl.gov.mc.protegosafe.data.model.covidtest

import com.google.gson.annotations.SerializedName

data class TestSubscriptionStatusRequestBody(
    @SerializedName("guid")
    val guid: String
)
