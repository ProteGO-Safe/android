package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.TrackingRepository

class GetTrackingAgreementStatusUseCase(
    private val trackingRepository: TrackingRepository
) {
    fun execute(): Boolean {
        return trackingRepository.isTrackingAccepted()
    }
}