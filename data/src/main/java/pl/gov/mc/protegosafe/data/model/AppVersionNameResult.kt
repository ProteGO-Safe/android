package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class AppVersionNameResult(
    @SerializedName("appVersion")
    val versionName: String
)
