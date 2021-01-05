package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ENStatsData(
    @SerializedName("lastRiskCheckTimestamp")
    val lastRiskCheckTimestamp: Long,
    @SerializedName("todayKeysCount")
    val todayKeysCount: Long,
    @SerializedName("last7daysKeysCount")
    val last7daysKeysCount: Long,
    @SerializedName("totalKeysCount")
    val totalKeysCount: Long
)
