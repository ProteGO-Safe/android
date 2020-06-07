package pl.gov.mc.protegosafe.domain.model.exposeNotification

data class TemporaryExposureKeysUploadRequestData(
    val keys: List<TemporaryExposureKeyItem>,
    val platform: String,
    val deviceVerificationPayload: String,
    val appPackageName: String,
    val regions: List<String>,
    val verificationPayload: String
)
