package pl.gov.mc.protegosafe.domain

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.TemporaryID

interface OpenTraceRepository {
    fun startBLEMonitoringService(delay: Long)
    fun stopBLEMonitoringService()
    fun getTemporaryIDs(): Completable
    fun getHandShakePin(): Completable
    fun retrieveTemporaryID(): TemporaryID
    fun setBLEBroadcastMessage(temporaryID: TemporaryID)
}