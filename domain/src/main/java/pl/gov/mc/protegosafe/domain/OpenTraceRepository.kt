package pl.gov.mc.protegosafe.domain

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem

interface OpenTraceRepository {
    fun startBLEMonitoringService(delay: Long)
    fun stopBLEMonitoringService()
    fun getTemporaryIDs(): Completable
    fun getHandShakePin(): Completable
    fun retrieveTemporaryID(): TemporaryIDItem
    fun setBLEBroadcastMessage(temporaryID: TemporaryIDItem)
}