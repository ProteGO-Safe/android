package pl.gov.mc.protego.bluetooth.scanner

import android.content.Context
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.exceptions.BleException
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.SerialDisposable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Function
import io.reactivex.observables.GroupedObservable
import io.reactivex.plugins.RxJavaPlugins
import pl.gov.mc.protego.bluetooth.PeripheralIgnoredTimeoutInSec
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdAgent
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdRemote
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class ProteGoScanner(context: Context, private val scannerListener: ScannerListener, private val beaconIdAgent: BeaconIdAgent) :
    ScannerInterface {

    private val client = RxBleClient.create(context)
    private val serialDisposable = SerialDisposable()

    init {
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException && throwable.cause is BleException) {
                return@setErrorHandler // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable)
        }
    }

    override fun enable(inMode: ScannerInterface.Mode) {
        Timber.d("[Scanner] enabling...")
        check(serialDisposable.get() == null) { "[Scanner] already enabled" }
        val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
        val transformer: ObservableTransformer<ClassifiedPeripheral.ProteGo, BeaconIdRemote> = when (inMode) {
            ScannerInterface.Mode.SCAN_ONLY -> scanOnlyBeaconId()
            ScannerInterface.Mode.SCAN_AND_EXCHANGE_BEACON_IDS -> exchangeBeaconId(ProteGoConnector(beaconIdAgent))
        }
        client.scanBleDevices(scanSettings)
            .map(ScanResultClassification)
            .ofType(ClassifiedPeripheral.ProteGo::class.java)
            .compose(transformer)
            .subscribe(
                { beaconIdAgent.synchronizedBeaconId(it) },
                {
                    scannerListener.error(this, it)
                    disable()
                }
            )
            .let { serialDisposable.set(it) }
        Timber.i("[Scanner] enabled")
    }

    override fun disable() {
        Timber.i("[Scanner] disabled")
        serialDisposable.set(null)
    }

    private fun scanOnlyBeaconId(): ObservableTransformer<ClassifiedPeripheral.ProteGo, BeaconIdRemote> =
        ObservableTransformer { proteGoScannedDevices ->
            proteGoScannedDevices.ofType(ClassifiedPeripheral.ProteGo.FullAdvertisement::class.java)
                .groupBy { fa -> fa.beaconId }
                .flatMap { it.throttleFirstPeripheralAutoCleanUp() }
                .map { BeaconIdRemote(it.beaconId, it.rssi) }
        }

    private fun exchangeBeaconId(proteGoConnector: ProteGoConnector): ObservableTransformer<ClassifiedPeripheral.ProteGo, BeaconIdRemote> =
        ObservableTransformer { classifiedScannedDevices ->
            classifiedScannedDevices.publish { csd ->
                Observable.merge(
                    csd.ofType(ClassifiedPeripheral.ProteGo.FullAdvertisement::class.java)
                        .groupBy { fa -> fa.beaconId }
                        .flatMap { it.throttleFirstPeripheralAutoCleanUp() },
                    Observable.merge(
                        csd.ofType(ClassifiedPeripheral.ProteGo.PotentialAdvertisement::class.java),
                        csd.ofType(ClassifiedPeripheral.ProteGo.MinimalAdvertisement::class.java)
                    )
                        .groupBy { ma -> ma.bleDevice.macAddress }
                        .flatMap { it.throttleFirstPeripheralAutoCleanUp() }
                )
            }
                .flatMap { proteGoPeripheral ->
                    val macAddress = proteGoPeripheral.bleDevice.macAddress
                    proteGoConnector.syncBeaconIds(proteGoPeripheral)
                        .doOnNext { Timber.d("[connection][$macAddress] ${it.className()}") }
                        .ofType(SyncEvent.Process.ReadBeaconId.Valid::class.java)
                        .map { it.beaconIdRemote }
                        .let {
                            if (proteGoPeripheral is ClassifiedPeripheral.ProteGo.FullAdvertisement)
                                it.startWithArray(
                                    BeaconIdRemote(
                                        proteGoPeripheral.beaconId,
                                        proteGoPeripheral.rssi
                                    )
                                )
                            else it
                        }
                }
        }

    private fun <T> GroupedObservable<*, T>.throttleFirstPeripheralAutoCleanUp() =
        Timeout(PeripheralIgnoredTimeoutInSec, TimeUnit.SECONDS).let {
            val timeoutPlusOneMinute = it.timeUnit.toSeconds(it.timespan).plus(60L)
            this.timeout(timeoutPlusOneMinute, TimeUnit.SECONDS)
                .onErrorResumeNext(Function { t ->
                    if (t is TimeoutException) Observable.empty()
                    else Observable.error(t)
                })
                .throttleFirst(it.timespan, it.timeUnit)
        }

    private data class Timeout(val timespan: Long, val timeUnit: TimeUnit)
}