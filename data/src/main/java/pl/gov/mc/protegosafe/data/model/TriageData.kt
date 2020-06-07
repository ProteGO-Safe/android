package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class TriageData(
    @SerializedName("timestamp")
    val timestamp: Long
)
