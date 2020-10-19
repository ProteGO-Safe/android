package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

class CloseAppData(
    @SerializedName("turnOff")
    val turnOff: Boolean
)
