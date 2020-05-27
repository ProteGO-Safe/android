package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class AppLifecycleStateResult(
    @SerializedName("appState")
    val appLifecycleState: Int
)
