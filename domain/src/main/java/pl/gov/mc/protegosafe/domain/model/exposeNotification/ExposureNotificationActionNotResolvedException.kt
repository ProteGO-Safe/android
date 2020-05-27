package pl.gov.mc.protegosafe.domain.model.exposeNotification

import pl.gov.mc.protegosafe.domain.model.ResolutionRequest

data class ExposureNotificationActionNotResolvedException(
    val resolutionRequest: ResolutionRequest,
    val apiException: Throwable
) : Exception(apiException)
