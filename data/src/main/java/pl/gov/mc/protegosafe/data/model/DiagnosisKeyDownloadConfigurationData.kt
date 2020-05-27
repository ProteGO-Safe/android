package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class DiagnosisKeyDownloadConfigurationData(
    @SerializedName("timeoutMobileSeconds")
    val timeoutMobileSeconds: Long,
    @SerializedName("timeoutWifiSeconds")
    val timeoutWifiSeconds: Long,
    @SerializedName("retryCount")
    val retryCount: Long
)
