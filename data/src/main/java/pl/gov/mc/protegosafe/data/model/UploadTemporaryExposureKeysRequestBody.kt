package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class UploadTemporaryExposureKeysRequestBody(
    @SerializedName("isInteroperabilityEnabled")
    val isInteroperabilityEnabled: Boolean,
    @SerializedName("data")
    val data: TemporaryExposureKeysUploadData
)
