package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ClearData(
    @SerializedName("clearAll")
    val clearAll: Boolean
)
