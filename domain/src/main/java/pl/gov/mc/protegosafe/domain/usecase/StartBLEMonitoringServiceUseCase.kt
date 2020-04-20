package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.OpenTraceRepository

class StartBLEMonitoringServiceUseCase(
    private val openTraceRepository: OpenTraceRepository
) {

    fun execute(timeFromNowInMillis: Long) {
        openTraceRepository.scheduleStartMonitoringService(timeFromNowInMillis)
    }
}