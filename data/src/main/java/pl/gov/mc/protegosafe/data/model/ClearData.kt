package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ClearData(
    @SerializedName("clearBtData")
    val clearBtData: Boolean
)
