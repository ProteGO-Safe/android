package pl.gov.mc.protegosafe.domain

interface OpenTraceRepository {
    fun scheduleStartMonitoringService(timeFromNowInMillis: Long)
}