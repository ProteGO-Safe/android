package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Observable

interface DeviceRepository {
    fun isBtSupported(): Boolean
    fun isLocationEnabled(): Boolean
    fun isBtOn(): Boolean
    fun isBatteryOptimizationOn(): Boolean
    fun isNotificationEnabled(): Boolean
    fun isBtServiceOn(): Boolean
    fun getServicesStatusJson(): String
    val traceServiceEnabled: Observable<Boolean>
}