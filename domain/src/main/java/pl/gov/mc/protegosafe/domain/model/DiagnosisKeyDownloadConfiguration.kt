package pl.gov.mc.protegosafe.domain.model

data class DiagnosisKeyDownloadConfiguration(
    val timeoutMobileSeconds: Long,
    val timeoutWifiSeconds: Long,
    val retryCount: Long
)
