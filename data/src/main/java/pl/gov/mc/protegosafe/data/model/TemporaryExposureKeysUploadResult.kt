package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class TemporaryExposureKeysUploadResult(
    @SerializedName("result")
    val result: Int
)
