package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class TemporaryExposureKeysUploadData(
    @SerializedName("temporaryExposureKeys")
    val temporaryExposureKeys: List<TemporaryExposureKeyRequestData>,
    @SerializedName("platform")
    val platform: String,
    @SerializedName("appPackageName")
    val appPackageName: String,
    @SerializedName("regions")
    val regions: List<String>,
    @SerializedName("verificationPayload")
    val verificationPayload: String
)
