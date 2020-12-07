package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class DeleteActivitiesData(
    @SerializedName("notifications")
    val notifications: List<String>,
    @SerializedName("riskChecks")
    val riskChecks: List<String>,
    @SerializedName("exposures")
    val exposures: List<String>,
)
