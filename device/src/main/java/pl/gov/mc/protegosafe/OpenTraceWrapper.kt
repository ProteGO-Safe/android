package pl.gov.mc.protegosafe

import android.content.Context
import com.google.firebase.functions.FirebaseFunctions
import io.bluetrace.opentrace.BuildConfig
import io.bluetrace.opentrace.Utils
import io.bluetrace.opentrace.idmanager.TempIDManager
import io.bluetrace.opentrace.services.BluetoothMonitoringService
import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.OpenTraceRepository
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem
import pl.gov.mc.protegosafe.mapper.toDeviceModel
import pl.gov.mc.protegosafe.mapper.toDomainModel

class OpenTraceWrapper(private val context: Context) : OpenTraceRepository {
    override fun startBLEMonitoringService(delay: Long) {
        if (delay == 0L) {
            Utils.startBluetoothMonitoringService(context)
        } else {
            Utils.scheduleStartMonitoringService(context, delay)
        }
    }

    override fun stopBLEMonitoringService() {
        Utils.stopBluetoothMonitoringService(context)
    }

    override fun getTemporaryIDs() = Completable.fromAction() {
        val functions = FirebaseFunctions.getInstance(BuildConfig.FIREBASE_REGION)
        TempIDManager.getTemporaryIDs(context, functions) }

    override fun getHandShakePin() = Completable.fromAction {
        val functions = FirebaseFunctions.getInstance(BuildConfig.FIREBASE_REGION)
        Utils.getHandShakePin(context, functions)
    }

    override fun retrieveTemporaryID(): TemporaryIDItem {
        val tempId = TempIDManager.retrieveTemporaryID(context)
        checkNotNull(tempId)
        return tempId.toDomainModel()
    }

    override fun setBLEBroadcastMessage(temporaryID: TemporaryIDItem) {
        BluetoothMonitoringService.broadcastMessage = temporaryID.toDeviceModel()
    }
}