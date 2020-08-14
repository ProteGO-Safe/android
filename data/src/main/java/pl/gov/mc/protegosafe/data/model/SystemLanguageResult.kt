package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class SystemLanguageResult(
    @SerializedName("language")
    val language: String
)
