package pl.gov.mc.protego.bluetooth.scanner

import android.bluetooth.BluetoothGattCharacteristic
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.exceptions.BleAdapterDisabledException
import com.polidea.rxandroidble2.exceptions.BleGattCharacteristicException
import com.polidea.rxandroidble2.exceptions.BleGattException
import io.reactivex.Observable
import io.reactivex.Single
import pl.gov.mc.protego.bluetooth.ProteGoCharacteristicUUIDString
import pl.gov.mc.protego.bluetooth.ProteGoServiceUUIDString
import pl.gov.mc.protego.bluetooth.beacon.BeaconId
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdLocal
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdRemote
import timber.log.Timber
import java.util.*

// TODO: call this class from ProteGoScanner
// TODO: manage BeaconIdRemote and BeaconIdLocal
class ProteGoConnector {

    private val uuidProteGoService = UUID.fromString(ProteGoServiceUUIDString)
    private val uuidProteGoCharacteristic = UUID.fromString(ProteGoCharacteristicUUIDString)

    private sealed class DiscoveryProcess {
        object Started : DiscoveryProcess()
        sealed class Finished : DiscoveryProcess() {
            object NotFound : Finished()
            data class Found(val connection: RxBleConnection, val proteGoCharacteristic: BluetoothGattCharacteristic) : Finished()
        }
    }

    private fun ClassifiedPeripheral.ProteGo.exchangeBeaconIds(beaconIdLocal: BeaconIdLocal): Observable<SyncEvents> =
        this.bleDevice.establishConnection(true)
            .flatMap { connection ->
                connection.discoverServices()
                    .map<DiscoveryProcess> { services ->
                        services.bluetoothGattServices.find { it.uuid == uuidProteGoService }
                            ?.getCharacteristic(uuidProteGoCharacteristic)
                            ?.let { DiscoveryProcess.Finished.Found(connection, it) }
                            ?: DiscoveryProcess.Finished.NotFound
                    }
                    .toObservable()
                    .startWithArray(DiscoveryProcess.Started)
            }
            .flatMap {
                when (it) {
                    DiscoveryProcess.Started -> Observable.just(SyncEvents.Connection.DiscoveringServices)
                    DiscoveryProcess.Finished.NotFound -> Observable.just(SyncEvents.Process.End.Aborted)
                        .also { Timber.d("[connection] Abort: ProteGoCharacteristic not found for classified peripheral: ${this.className()}") }
                    is DiscoveryProcess.Finished.Found -> when (this) {
                        is ClassifiedPeripheral.ProteGo.FullAdvertisement -> it.writeLocalBeaconIdOnly(beaconIdLocal)
                        is ClassifiedPeripheral.ProteGo.MinimalAdvertisement,
                        is ClassifiedPeripheral.ProteGo.PotentialAdvertisement -> it.exchangeBeaconIds(beaconIdLocal)
                    }
                        .concatWith(Observable.just(SyncEvents.Process.End.Finished))
                        .startWithArray(SyncEvents.Process.Start)
                }
            }
            .defaultIfEmpty(SyncEvents.Process.End.Aborted)
            .startWithArray(SyncEvents.Connection.Connecting)
            .takeUntil { it is SyncEvents.Process.End }
            .onErrorReturn {
                when (it) {
                    is BleGattException -> SyncEvents.Connection.Error.Gatt(it.status)
                    is BleAdapterDisabledException -> SyncEvents.Connection.Error.AdapterOff
                    else -> throw it
                }
            }

    private fun DiscoveryProcess.Finished.Found.writeLocalBeaconIdOnly(beaconIdLocal: BeaconIdLocal): Observable<SyncEvents.Process> =
        this.writeLocalBeaconId(beaconIdLocal).toObservable().cast(SyncEvents.Process::class.java)

    private fun DiscoveryProcess.Finished.Found.exchangeBeaconIds(beaconIdLocal: BeaconIdLocal): Observable<SyncEvents.Process> =
        this.readRemoteBeaconId()
            .toObservable()
            .publish { readRemoteBeaconIdObs ->
                val writeLocalBeaconIdIfReadSuccessfulObs = readRemoteBeaconIdObs
                    .filter { it is SyncEvents.Process.ReadBeaconId.Valid }
                    .flatMapSingle { writeLocalBeaconId(beaconIdLocal) }
                Observable.concatArray(
                    readRemoteBeaconIdObs,
                    writeLocalBeaconIdIfReadSuccessfulObs
                )
            }

    private fun DiscoveryProcess.Finished.Found.writeLocalBeaconId(beaconIdLocal: BeaconIdLocal): Single<SyncEvents.Process.WrittenBeaconId> =
        this.connection.writeCharacteristic(this.proteGoCharacteristic, beaconIdLocal.beaconId.byteArray)
            .map<SyncEvents.Process.WrittenBeaconId> { SyncEvents.Process.WrittenBeaconId.Success }
            .onErrorReturn {
                if (it is BleGattCharacteristicException) SyncEvents.Process.WrittenBeaconId.Failure(it.status)
                else throw it
            }

    private fun DiscoveryProcess.Finished.Found.readRemoteBeaconId(): Single<SyncEvents.Process.ReadBeaconId> =
        this.connection.readCharacteristic(this.proteGoCharacteristic)
            .map { bytes ->
                if (bytes.size == BeaconId.byteCount) {
                    val beaconId = BeaconId(bytes, 0)
                    val remoteBeaconId = BeaconIdRemote(beaconId, Date())
                    SyncEvents.Process.ReadBeaconId.Valid(remoteBeaconId)
                } else {
                    SyncEvents.Process.ReadBeaconId.Invalid
                }
            }
            .onErrorReturn {
                if (it is BleGattCharacteristicException) SyncEvents.Process.ReadBeaconId.Failure(it.status)
                else throw it
            }

    private sealed class SyncEvents {
        sealed class Connection : SyncEvents() {
            object Connecting : Connection()
            object DiscoveringServices : Connection()
            sealed class Error : Connection() {
                data class Gatt(val status: Int) : Error()
                object AdapterOff : Error()
            }
        }

        sealed class Process : SyncEvents() {
            object Start : Process()

            sealed class ReadBeaconId : Process() {
                data class Valid(val beaconIdRemote: BeaconIdRemote) : ReadBeaconId()
                object Invalid : ReadBeaconId()
                data class Failure(val status: Int) : ReadBeaconId()
            }

            sealed class WrittenBeaconId : Process() {
                object Success : WrittenBeaconId()
                data class Failure(val status: Int) : WrittenBeaconId()
            }

            sealed class End : Process() {
                object Aborted : End()
                object Finished : End()
            }
        }
    }
}