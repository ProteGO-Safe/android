package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem
import java.io.File

interface OpenTraceRepository {
    fun startBLEMonitoringService(delay: Long)
    fun stopBLEMonitoringService()
    fun getBLEServiceStatus(): Boolean
    fun getTemporaryIDs(): Completable
    fun getHandShakePin(): Completable
    fun retrieveTemporaryID(): TemporaryIDItem
    fun setBLEBroadcastMessage(temporaryID: TemporaryIDItem)
    fun dumpTraceData(uploadToken: String): Single<File>
    val trackTempId: Observable<String>
}