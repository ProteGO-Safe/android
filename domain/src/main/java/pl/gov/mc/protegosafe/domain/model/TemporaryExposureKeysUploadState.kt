package pl.gov.mc.protegosafe.domain.model

enum class TemporaryExposureKeysUploadState(val code: Int) {
    SUCCESS(1),
    FAILURE(2),
    OTHER(3),
}
