package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.rxjava3.core.Single
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository

class GetServicesStatusUseCase(
    private val deviceRepository: DeviceRepository
) {

    fun execute(): Single<String> = deviceRepository.getServicesStatusJson()
}
