package pl.gov.mc.protegosafe.data.model.covidtest

import com.google.gson.annotations.SerializedName

data class TestSubscriptionRequestData(
    @SerializedName("guid")
    val guid: String,
    @SerializedName("code")
    val code: String,
)
