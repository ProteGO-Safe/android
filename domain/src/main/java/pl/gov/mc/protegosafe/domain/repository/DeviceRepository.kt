package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationStatusItem

interface DeviceRepository {
    fun getServicesStatusJson(): Single<String>
    fun getExposureNotificationStatus(): Single<ExposureNotificationStatusItem>
}
