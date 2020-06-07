package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class GetAccessTokenResponseData(
    @SerializedName("accessToken")
    val token: String
)
