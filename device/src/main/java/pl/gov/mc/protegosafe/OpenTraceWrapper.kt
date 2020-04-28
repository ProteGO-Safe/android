package pl.gov.mc.protegosafe

import android.content.Context
import com.google.firebase.functions.FirebaseFunctions
import io.bluetrace.opentrace.Utils
import io.bluetrace.opentrace.idmanager.TempIDManager
import io.bluetrace.opentrace.services.BluetoothMonitoringService
import io.bluetrace.opentrace.status.persistence.StatusRecordStorage
import io.bluetrace.opentrace.streetpass.persistence.StreetPassRecordStorage
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository
import pl.gov.mc.protegosafe.mapper.toCompletable
import pl.gov.mc.protegosafe.mapper.toDeviceModel
import pl.gov.mc.protegosafe.mapper.toDomainModel
import pl.gov.mc.protegosafe.trace.notifications.ServiceStatusDataStore

class OpenTraceWrapper(
    private val context: Context,
    private val functions: FirebaseFunctions
) : OpenTraceRepository {
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

    override fun getTemporaryIDs() =
        TempIDManager.getTemporaryIDs(context, functions).toCompletable()

    override fun getHandShakePin() =
        Utils.getHandShakePin(context, functions).toCompletable()

    override fun retrieveTemporaryID(): TemporaryIDItem {
        val tempId = TempIDManager.retrieveTemporaryID(context)
        checkNotNull(tempId)
        return tempId.toDomainModel()
    }

    override fun setBLEBroadcastMessage(temporaryID: TemporaryIDItem) {
        BluetoothMonitoringService.broadcastMessage = temporaryID.toDeviceModel()
    }

    override fun getBLEServiceStatus(): Boolean {
        return ServiceStatusDataStore.isWorking
    }

    override fun clearTracingData() {
        StreetPassRecordStorage(context).nukeDb()
        StatusRecordStorage(context).nukeDb()
    }
}