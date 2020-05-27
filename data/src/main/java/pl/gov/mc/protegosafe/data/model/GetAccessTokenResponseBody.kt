package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class GetAccessTokenResponseBody(
    @SerializedName("accessToken")
    val token: String
)
