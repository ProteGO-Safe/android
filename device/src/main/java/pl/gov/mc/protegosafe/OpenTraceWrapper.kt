package pl.gov.mc.protegosafe

import android.content.Context
import android.os.Build
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import io.bluetrace.opentrace.TracerApp
import io.bluetrace.opentrace.Utils
import io.bluetrace.opentrace.fragment.ExportData
import io.bluetrace.opentrace.idmanager.TempIDManager
import io.bluetrace.opentrace.services.BluetoothMonitoringService
import io.bluetrace.opentrace.status.persistence.StatusRecord
import io.bluetrace.opentrace.status.persistence.StatusRecordStorage
import io.bluetrace.opentrace.streetpass.persistence.StreetPassRecord
import io.bluetrace.opentrace.streetpass.persistence.StreetPassRecordStorage
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository
import pl.gov.mc.protegosafe.mapper.TempIdJsonSerializer
import pl.gov.mc.protegosafe.mapper.toCompletable
import pl.gov.mc.protegosafe.mapper.toDeviceModel
import pl.gov.mc.protegosafe.mapper.toDomainModel
import pl.gov.mc.protegosafe.trace.notifications.ServiceStatusDataStore
import java.io.File
import java.io.FileOutputStream

class OpenTraceWrapper(
    private val context: Context,
    private val functions: FirebaseFunctions,
    private val jsonSerializer: TempIdJsonSerializer
) : OpenTraceRepository {
    init {
        TempIDManager.setOnTempIdUpdate { tempIdSubject.onNext(it) }
    }

    private val tempIdSubject: BehaviorSubject<String> = BehaviorSubject.create()

    override val trackTempId: Observable<String>
        get() = tempIdSubject.hide().distinctUntilChanged()

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

    override fun retrieveTemporaryIDJson(): String =
        jsonSerializer.toJson(retrieveTemporaryID().tempID)

    override fun setBLEBroadcastMessage(temporaryID: TemporaryIDItem) {
        BluetoothMonitoringService.broadcastMessage = temporaryID.toDeviceModel()
    }

    override fun getBLEServiceStatus(): Boolean {
        return ServiceStatusDataStore.isWorking
    }

    override fun dumpTraceData(uploadToken: String): Single<File> {
        //OpenTrace code to fetch data from database and store in json file
        val observableStreetRecords = Observable.create<List<StreetPassRecord>> {
            val result = StreetPassRecordStorage(TracerApp.AppContext).getAllRecords()
            it.onNext(result)
        }
        val observableStatusRecords = Observable.create<List<StatusRecord>> {
            val result = StatusRecordStorage(TracerApp.AppContext).getAllRecords()
            it.onNext(result)
        }

        return Observable.zip(observableStreetRecords, observableStatusRecords,
            BiFunction<List<StreetPassRecord>, List<StatusRecord>, ExportData> { records, status ->
                ExportData(
                    records,
                    status
                )
            }
        ).map { exportData ->
            val date = Utils.getDateFromUnix(System.currentTimeMillis())
            val gson = Gson()

            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val updatedDeviceList = exportData.recordList.map {
                it.timestamp = it.timestamp / 1000
                it
            }

            val updatedStatusList = exportData.statusList.map {
                it.timestamp = it.timestamp / 1000
                it
            }

            val map: MutableMap<String, Any> = HashMap()
            map["token"] = uploadToken as Any
            map["records"] = updatedDeviceList as Any
            map["events"] = updatedStatusList as Any

            val mapString = gson.toJson(map)

            val fileName = "StreetPassRecord_${manufacturer}_${model}_$date.json"
            val fileOutputStream: FileOutputStream

            val uploadDir = File(context.filesDir, "upload")

            if (uploadDir.exists()) {
                uploadDir.deleteRecursively()
            }

            uploadDir.mkdirs()
            val fileToUpload = File(uploadDir, fileName)
            fileOutputStream = FileOutputStream(fileToUpload)

            fileOutputStream.write(mapString.toByteArray())
            fileOutputStream.close()
            fileToUpload
        }.firstOrError()
    }

    override fun clearTracingData() {
        StreetPassRecordStorage(context).nukeDb()
        StatusRecordStorage(context).nukeDb()
    }
}