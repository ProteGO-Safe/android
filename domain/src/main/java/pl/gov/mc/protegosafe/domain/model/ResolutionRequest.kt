package pl.gov.mc.protegosafe.domain.model

enum class ResolutionRequest(val code: Int) {
    START_EXPOSURE_NOTIFICATION(100),
    ACCESS_TEMPORARY_EXPOSURE_KEYS(101)
}
