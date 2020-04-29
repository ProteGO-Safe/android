package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

class GetCurrentTemporaryIDUseCase(
    private val openTraceRepository: OpenTraceRepository
) {
    fun execute(): String = openTraceRepository.retrieveTemporaryIDJson()
}