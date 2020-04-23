package pl.gov.mc.protegosafe.domain.repository

interface TrackingRepository {
    fun isTrackingAccepted(): Boolean
    fun saveTrackingAgreement(isAccepted: Boolean)
}