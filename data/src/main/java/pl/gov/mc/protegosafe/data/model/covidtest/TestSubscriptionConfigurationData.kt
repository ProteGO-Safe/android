package pl.gov.mc.protegosafe.data.model.covidtest

import com.google.gson.annotations.SerializedName

data class TestSubscriptionConfigurationData(
    @SerializedName("interval")
    val interval: Long
)
