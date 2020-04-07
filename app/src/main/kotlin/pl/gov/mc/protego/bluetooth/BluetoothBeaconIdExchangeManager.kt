package pl.gov.mc.protego.bluetooth

import android.bluetooth.BluetoothManager
import android.content.Context
import pl.gov.mc.protego.bluetooth.advertiser.*
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdAgent
import pl.gov.mc.protego.bluetooth.scanner.ProteGoScanner
import pl.gov.mc.protego.bluetooth.scanner.ScannerInterface
import pl.gov.mc.protego.bluetooth.scanner.ScannerListener
import timber.log.Timber


class BluetoothBeaconIdExchangeManager(context: Context, bluetoothManager: BluetoothManager, beaconIdAgent: BeaconIdAgent) {

    private fun timberWithLocalTag() = Timber.tag("[BT_MNG]")

    private val proteGoAdvertiser =
        ProteGoAdvertiser(context, bluetoothManager, beaconIdAgent, object : AdvertiserListener {
            override fun error(advertiserInterface: AdvertiserInterface, advertiserError: AdvertiserListener.AdvertiserError) {
                timberWithLocalTag().e("advertiser error: $advertiserError")
            }
        })
    private var scannerInitialized = false
    private val proteGoScanner: ProteGoScanner by lazy {
        scannerInitialized = true
        ProteGoScanner(context, object : ScannerListener {
            override fun error(scannerInterface: ScannerInterface, throwable: Throwable) {
                timberWithLocalTag().e(throwable, "scanner error")
            }
        }, beaconIdAgent)
    }

    fun start(mode: Mode = Mode.BEST_EFFORT) {
        // TODO: handling BT / Location / Permissions ON/OFF
        when (mode) {
            Mode.CONNECT_ONLY -> proteGoScanner.enable(ScannerInterface.Mode.SCAN_AND_EXCHANGE_BEACON_IDS)
            Mode.BEST_EFFORT -> when (val enableResult = proteGoAdvertiser.enable()) {
                EnableResult.PreconditionFailure.AlreadyEnabled,
                EnableResult.PreconditionFailure.CannotObtainBluetoothAdapter,
                EnableResult.PreconditionFailure.BluetoothOff -> timberWithLocalTag().e("fatal error: $enableResult")
                is EnableResult.Started -> {
                    timberWithLocalTag().i("advertiser enabled: $enableResult")
                    val scannerMode = if (
                        enableResult.advertiserResult == AdvertiserResult.Success
                        && enableResult.serverResult is ServerResult.Success
                    ) {
                        ScannerInterface.Mode.SCAN_ONLY
                    } else {
                        ScannerInterface.Mode.SCAN_AND_EXCHANGE_BEACON_IDS
                    }
                    proteGoScanner.enable(scannerMode)
                    if (enableResult.advertiserResult !is AdvertiserResult.Success) {
                        // no use for advertiser nor server
                        proteGoAdvertiser.disable()
                    }
                }
            }
        }
    }

    fun stop() {
        proteGoAdvertiser.disable()
        if (scannerInitialized) proteGoScanner.disable()
    }

    enum class Mode {
        BEST_EFFORT, CONNECT_ONLY
    }
}