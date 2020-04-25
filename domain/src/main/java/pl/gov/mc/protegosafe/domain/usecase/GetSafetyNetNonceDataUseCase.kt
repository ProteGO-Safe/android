package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

class GetSafetyNetNonceDataUseCase (
    private val openTraceRepository: OpenTraceRepository
) {
    fun execute() : String {
        // TODO: Fix retrieveTemporaryID crash at application start.
        val temporaryId = try {
            openTraceRepository.retrieveTemporaryID().tempID
        } catch (ex: Exception) {
            "ProteGo Nonce: ${System.currentTimeMillis()}"
        }
        return temporaryId + System.currentTimeMillis()
    }
}