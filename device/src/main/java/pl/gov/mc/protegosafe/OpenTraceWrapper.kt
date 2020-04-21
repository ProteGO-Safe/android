package pl.gov.mc.protegosafe

import android.content.Context
import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.OpenTraceRepository
import pl.gov.mc.protegosafe.domain.model.TemporaryID

class OpenTraceWrapper(private val context: Context) : OpenTraceRepository {
    //TODO-OpenTrace impl
    override fun startBLEMonitoringService(delay: Long) {
        //Impl
        if (delay == 0L) {
            Utils.startBluetoothMonitoringService(context)
        } else {
            Utils.scheduleStartMonitoringService(context, delay)
        }
    }

    override fun stopBLEMonitoringService() {
        //Utils.stopBluetoothMonitoringService(context: Context)
    }

    override fun getTemporaryIDs(): Completable {
//        private val functions = FirebaseFunctions.getInstance(BuildConfig.FIREBASE_REGION)
//        TempIDManager.getTemporaryIDs(context, functions)
        return Completable.complete()
    }

    override fun getHandShakePin(): Completable {
//        private val functions = FirebaseFunctions.getInstance(BuildConfig.FIREBASE_REGION)
//        Utils.getHandShakePin(context, functions)
        return Completable.complete()
    }

    override fun retrieveTemporaryID(): TemporaryID {
        //TempIDManager.retrieveTemporaryID(context)
        return TemporaryID(0, "DUMMY", 0L)
    }

    override fun setBLEBroadcastMessage(temporaryID: TemporaryID) {
//        BluetoothMonitoringService.broadcastMessage = temporaryID.toData()
    }
}