package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class SendSmsData(
    @SerializedName("number") val number: String,
    @SerializedName("text") val text: String,
)
