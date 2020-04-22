package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.DeviceRepository

class GetServicesStatusUseCase(
    private val deviceRepository: DeviceRepository
) {

    fun execute(): String = deviceRepository.getServicesStatusJson()
}