package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable

interface TrackingRepository {
    fun isTrackingAccepted(): Boolean
    fun saveTrackingAgreement(isAccepted: Boolean): Completable
}