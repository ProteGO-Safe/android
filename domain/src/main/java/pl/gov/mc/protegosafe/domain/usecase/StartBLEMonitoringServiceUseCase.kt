package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

class StartBLEMonitoringServiceUseCase(
    private val openTraceRepository: OpenTraceRepository
) {

    fun execute(delayMs: Long = 0) {
        openTraceRepository.startBLEMonitoringService(delayMs)
    }
}