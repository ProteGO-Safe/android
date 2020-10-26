package pl.gov.mc.protegosafe.data.model.covidtest

import com.google.gson.annotations.SerializedName

data class SubscriptionStatusResponseData(
    @SerializedName("guid")
    val guid: String,
    @SerializedName("status")
    val status: Int
)
