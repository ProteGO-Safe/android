package pl.gov.mc.protegosafe.data

import pl.gov.mc.protegosafe.data.db.TrackingDataStore
import pl.gov.mc.protegosafe.domain.repository.TrackingRepository

class TrackingRepositoryImpl(
    private val trackingDataStore: TrackingDataStore
): TrackingRepository {
    override fun isTrackingAccepted(): Boolean {
        return trackingDataStore.isTrackingAccepted
    }

    override fun saveTrackingAgreement(isAccepted: Boolean) {
        trackingDataStore.isTrackingAccepted = isAccepted
    }
}