package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class GetAccessTokenRequestData(@SerializedName("code") val code: String)
