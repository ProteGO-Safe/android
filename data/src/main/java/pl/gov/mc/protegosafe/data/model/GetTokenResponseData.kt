package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class GetTokenResponseData(
    @SerializedName("token")
    val token: String
)
