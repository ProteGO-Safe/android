package pl.gov.mc.protegosafe.model

import com.google.gson.annotations.SerializedName

data class TraceStatusDto (
    @SerializedName("enableBtService")
    val enableBtService: Boolean
)