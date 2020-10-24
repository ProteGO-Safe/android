package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class SimpleResult(
    @SerializedName("result")
    val result: Int
)
