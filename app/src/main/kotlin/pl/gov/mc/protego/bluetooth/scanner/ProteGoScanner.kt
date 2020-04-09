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
import io.reactivex.subjects.BehaviorSubject
import pl.gov.mc.protego.bluetooth.PeripheralIgnoredGracePeriodIfNoProteGoCharacteristicInMin
import pl.gov.mc.protego.bluetooth.PeripheralIgnoredTimeoutInSec
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdAgent
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdRemote
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdSource
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class ProteGoScanner(context: Context, private val beaconIdAgent: BeaconIdAgent) : ScannerInterface {

    private val client = RxBleClient.create(context)
    private val serialDisposable = SerialDisposable()
    private fun timberWithLocalTag() = Timber.tag("[scanner]")

    init {
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException && throwable.cause is BleException) {
                return@setErrorHandler // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable)
        }
    }

    // region General enable / disable functions ----------------------------------------------------------

    override fun enable(inMode: ScannerInterface.Mode, listener: ScannerListener) {
        timberWithLocalTag().i("enabling...")
        check(serialDisposable.get() == null) { "[scanner] already enabled" }
        val scanMode = when (inMode) {
            ScannerInterface.Mode.SCAN_ONLY -> ScanSettings.SCAN_MODE_LOW_POWER
            ScannerInterface.Mode.SCAN_AND_EXCHANGE_BEACON_IDS -> ScanSettings.SCAN_MODE_BALANCED
        }
        val scanSettings = ScanSettings.Builder().setScanMode(scanMode).build()
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
                    listener.error(this, it)
                    disable()
                }
            )
            .let { serialDisposable.set(it) }
        timberWithLocalTag().i("enabled")
    }

    override fun disable() {
        timberWithLocalTag().i("disabled")
        serialDisposable.set(null)
    }

    // endregion

    // region Private functions ----------------------------------------------------------

    private fun scanOnlyBeaconId(): ObservableTransformer<ClassifiedPeripheral.ProteGo, BeaconIdRemote> =
        ObservableTransformer { proteGoScannedDevices ->
            proteGoScannedDevices.ofType(ClassifiedPeripheral.ProteGo.FullAdvertisement::class.java)
                .groupBy { fa -> fa.beaconId }
                .flatMap { it.throttleFirstPeripheralAutoCleanUp() }
                .map { BeaconIdRemote(it.beaconId, it.rssi, BeaconIdSource.SCANNER) }
        }

    private fun exchangeBeaconId(proteGoConnector: ProteGoConnector): ObservableTransformer<ClassifiedPeripheral.ProteGo, BeaconIdRemote> =
        ObservableTransformer { classifiedScannedDevices ->
            classifiedScannedDevices.publish { csd ->
                Observable.merge(
                    csd.ofType(ClassifiedPeripheral.ProteGo.FullAdvertisement::class.java)
                        .groupBy { fa -> fa.beaconId }
                        .flatMap { it.syncAtMostOnceEveryMinute(proteGoConnector) },
                    csd.ofType(ClassifiedPeripheral.ProteGo.MinimalAdvertisement::class.java)
                        .groupBy { ma -> ma.bleDevice.macAddress }
                        .flatMap { it.syncAtMostOnceEveryMinute(proteGoConnector) },
                    csd.ofType(ClassifiedPeripheral.ProteGo.PotentialAdvertisement::class.java)
                        .groupBy { pa -> pa.bleDevice.macAddress }
                        .flatMap({ it.syncAtMostOnceEveryMinuteIfValidProteGoElseIgnore(proteGoConnector) }, 2)
                )
                    .takeOnlyValidReadBeacons()
            }
        }

    /**
     * This function sync emissions from a grouped observable at most once every minute. If syncs will fail due to peripheral not being
     * a valid ProteGO peripheral no more syncs will happen for time specified in Constants file
     */
    private fun GroupedObservable<*, ClassifiedPeripheral.ProteGo.PotentialAdvertisement>.syncAtMostOnceEveryMinuteIfValidProteGoElseIgnore(
        proteGoConnector: ProteGoConnector
    ): Observable<SyncEvent> {
        val enterGracePeriod = BehaviorSubject.create<Unit>()
        fun isInGracePeriod() = enterGracePeriod.value?.let { true } ?: false
        return this
            .share()
            .let { shared ->
                val disposable = shared
                    .take(PeripheralIgnoredGracePeriodIfNoProteGoCharacteristicInMin, TimeUnit.MINUTES)
                    .delaySubscription(enterGracePeriod)
                    .ignoreElements()
                    .onErrorComplete()
                    .subscribe()
                shared.doOnDispose {
                    if (enterGracePeriod.value == null) disposable.dispose()
                }
            }
            .filter { !isInGracePeriod() }
            .syncAtMostOnceEveryMinute(proteGoConnector)
            .doOnNext { if (it is SyncEvent.Process.End.NoProteGoAttributes) enterGracePeriod.onNext(Unit) }
    }

    private fun <T : ClassifiedPeripheral.ProteGo> Observable<T>.syncAtMostOnceEveryMinute(proteGoConnector: ProteGoConnector) =
        this.throttleFirstPeripheralAutoCleanUp()
            .flatMap { proteGoPeripheral ->
                val macAddress = proteGoPeripheral.bleDevice.macAddress
                proteGoConnector.syncBeaconIds(proteGoPeripheral)
                    .doOnNext { evt -> timberWithLocalTag().d("[connection][$macAddress] ${evt.className()}") }
            }

    private fun <T> Observable<T>.throttleFirstPeripheralAutoCleanUp() =
        Timeout(PeripheralIgnoredTimeoutInSec, TimeUnit.SECONDS).let {
            val timeoutPlusOneMinute = it.timeUnit.toSeconds(it.timespan).plus(60L)
            this.timeout(timeoutPlusOneMinute, TimeUnit.SECONDS)
                .onErrorResumeNext(Function { t ->
                    if (t is TimeoutException) Observable.empty()
                    else Observable.error(t)
                })
                .throttleFirst(it.timespan, it.timeUnit)
        }

    private fun Observable<SyncEvent>.takeOnlyValidReadBeacons() =
        this.ofType(SyncEvent.Process.ReadBeaconId.Valid::class.java)
            .map { it.beaconIdRemote }

    private data class Timeout(val timespan: Long, val timeUnit: TimeUnit)

    // endregion
}