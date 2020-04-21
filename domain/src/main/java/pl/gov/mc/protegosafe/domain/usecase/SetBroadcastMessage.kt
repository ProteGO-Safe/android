package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.OpenTraceRepository

class SetBroadcastMessage(
    private val openTraceRepository: OpenTraceRepository
) {
    fun execute(){
        val temporaryId = openTraceRepository.retrieveTemporaryID()
        openTraceRepository.setBLEBroadcastMessage(temporaryId)
    }
}