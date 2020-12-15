package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class CovidStatsNotificationsStatusData(
    @SerializedName("isCovidStatsNotificationEnabled")
    val isCovidStatsNotificationEnabled: Boolean
)
