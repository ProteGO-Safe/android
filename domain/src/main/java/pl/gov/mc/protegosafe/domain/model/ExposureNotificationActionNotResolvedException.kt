package pl.gov.mc.protegosafe.domain.model

data class ExposureNotificationActionNotResolvedException(
    val resolutionRequest: ResolutionRequest,
    val apiException: Throwable
) : Exception(apiException)
