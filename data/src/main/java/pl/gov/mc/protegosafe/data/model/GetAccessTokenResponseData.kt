package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class GetAccessTokenResponseData(
    @SerializedName("token")
    val token: String
)
