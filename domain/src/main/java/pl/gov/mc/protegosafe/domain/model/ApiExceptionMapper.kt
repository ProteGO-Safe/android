package pl.gov.mc.protegosafe.domain.model

import pl.gov.mc.protegosafe.domain.model.exposeNotification.ExposureNotificationActionNotResolvedException

interface ApiExceptionMapper {
    fun toStatus(throwable: Throwable): ExposureNotificationStatusItem
    fun toExposureNotificationActionNotResolvedException(
        throwable: Throwable,
        resolutionRequest: ResolutionRequest
    ): ExposureNotificationActionNotResolvedException?
}
