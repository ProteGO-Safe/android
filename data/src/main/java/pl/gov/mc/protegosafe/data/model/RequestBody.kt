package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class RequestBody(
    @SerializedName("data")
    val data: Any
)
