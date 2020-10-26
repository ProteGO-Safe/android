package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

class TestSubscriptionPinData(
    @SerializedName("code")
    val pin: String?
)
