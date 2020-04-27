package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

class StopBLEMonitoringServiceUseCase(
    private val openTraceRepository: OpenTraceRepository
) {
    fun execute() {
        openTraceRepository.stopBLEMonitoringService()
    }
}