package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ActivitiesResultData(
    @SerializedName("notifications")
    val notifications: List<NotificationActivityData>,
    @SerializedName("riskChecks")
    val riskChecks: List<RiskCheckActivityData>,
    @SerializedName("exposures")
    val exposures: List<ExposureCheckActivityData>
)
