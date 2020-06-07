package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ResponseBody<T>(
    @SerializedName("result")
    val result: T
)
