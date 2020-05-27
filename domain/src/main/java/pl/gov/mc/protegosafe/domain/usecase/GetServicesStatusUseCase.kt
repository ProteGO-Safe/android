package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository

class GetServicesStatusUseCase(
    private val deviceRepository: DeviceRepository
) {

    fun execute(): Single<String> = deviceRepository.getServicesStatusJson()
}
