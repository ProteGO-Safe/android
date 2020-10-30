package pl.gov.mc.protegosafe.data.model.covidtest

import com.google.gson.annotations.SerializedName

data class TestSubscriptionStatusData(
    @SerializedName("guid")
    val guid: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("updated")
    val updated: Long
)
