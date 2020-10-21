package pl.gov.mc.protegosafe.domain.model

enum class TemporaryExposureKeysUploadState(val code: Int) {
    SUCCESS(1),
    FAILURE(2),
    CANCELED(3),
    ACCESS_DENIED(5)
}
