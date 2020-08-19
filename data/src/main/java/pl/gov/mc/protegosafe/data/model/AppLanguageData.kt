package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class AppLanguageData(
    @SerializedName("language")
    val language: String
)
