package pl.gov.mc.protegosafe.domain.model

interface ApiExceptionMapper {
    fun toStatus(throwable: Throwable): ExposureNotificationStatusItem
    fun toExposureNotificationActionNotResolvedException(
        throwable: Throwable,
        resolutionRequest: ResolutionRequest
    ): ExposureNotificationActionNotResolvedException?
}
