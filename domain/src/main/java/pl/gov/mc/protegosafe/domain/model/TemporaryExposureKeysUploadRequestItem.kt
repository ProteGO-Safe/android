package pl.gov.mc.protegosafe.domain.model

data class TemporaryExposureKeysUploadRequestItem(
    val keys: List<TemporaryExposureKeyItem>,
    val isInteroperabilityEnabled: Boolean,
    val platform: String,
    val appPackageName: String,
    val regions: List<String>,
    val verificationPayload: String
)
