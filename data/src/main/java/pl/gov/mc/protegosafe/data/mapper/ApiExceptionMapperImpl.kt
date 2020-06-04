package pl.gov.mc.protegosafe.data.mapper

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import pl.gov.mc.protegosafe.domain.model.ApiExceptionMapper
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationStatusItem
import pl.gov.mc.protegosafe.domain.model.ResolutionRequest
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException

class ApiExceptionMapperImpl : ApiExceptionMapper {
    override fun toStatus(throwable: Throwable): ExposureNotificationStatusItem {
        return (throwable as? ApiException)?.let {
            if (it.statusCode == CommonStatusCodes.API_NOT_CONNECTED) {
                ExposureNotificationStatusItem.NOT_SUPPORTED
            } else {
                ExposureNotificationStatusItem.OFF
            }
        } ?: ExposureNotificationStatusItem.OFF
    }

    override fun toExposureNotificationActionNotResolvedException(
        throwable: Throwable,
        resolutionRequest: ResolutionRequest
    ): ExposureNotificationActionNotResolvedException? =
        if (throwable is ApiException &&
            throwable.statusCode == CommonStatusCodes.RESOLUTION_REQUIRED
        ) {
            ExposureNotificationActionNotResolvedException(
                resolutionRequest,
                throwable
            )
        } else {
            null
        }
}
