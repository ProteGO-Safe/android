package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem

interface OpenTraceRepository {
    fun startBLEMonitoringService(delay: Long)
    fun stopBLEMonitoringService()
    fun getBLEServiceStatus(): Boolean
    fun getTemporaryIDs(): Completable
    fun getHandShakePin(): Completable
    fun retrieveTemporaryID(): TemporaryIDItem
    fun setBLEBroadcastMessage(temporaryID: TemporaryIDItem)
    fun clearTracingData()
}