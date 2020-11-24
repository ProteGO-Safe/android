package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class InteroperabilityData(
    @SerializedName("isInteroperabilityEnabled")
    val isInteroperabilityEnabled: Boolean
)
