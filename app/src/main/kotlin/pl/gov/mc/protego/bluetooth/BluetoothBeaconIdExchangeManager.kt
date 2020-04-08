package pl.gov.mc.protego.bluetooth

import pl.gov.mc.protego.bluetooth.advertiser.*
import pl.gov.mc.protego.bluetooth.scanner.ScannerInterface
import pl.gov.mc.protego.bluetooth.scanner.ScannerListener
import timber.log.Timber


class BluetoothBeaconIdExchangeManager(
    private val advertiser: AdvertiserInterface,
    private val scanner: ScannerInterface
) {

    private fun timberWithLocalTag() = Timber.tag("[BT_MNG]")

    private val advertiserListener = object : AdvertiserListener {
        override fun error(advertiserInterface: AdvertiserInterface, advertiserError: AdvertiserListener.AdvertiserError) {
            timberWithLocalTag().e("advertiser error: $advertiserError")
        }
    }
    private val scannerListener = object : ScannerListener {
        override fun error(scannerInterface: ScannerInterface, throwable: Throwable) {
            timberWithLocalTag().e(throwable, "scanner error")
        }
    }

    fun start(mode: Mode = Mode.BEST_EFFORT) {
        // TODO: handling BT / Location / Permissions ON/OFF
        when (mode) {
            Mode.CONNECT_ONLY -> scanner.enable(ScannerInterface.Mode.SCAN_AND_EXCHANGE_BEACON_IDS, scannerListener)
            Mode.BEST_EFFORT -> when (val enableResult = advertiser.enable(advertiserListener)) {
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
                    timberWithLocalTag().i("enabling scanner in mode: $scannerMode")
                    scanner.enable(scannerMode, scannerListener)
                    if (enableResult.advertiserResult !is AdvertiserResult.Success) {
                        // no use for advertiser nor server
                        timberWithLocalTag().i("advertiser not functional, disabling...")
                        advertiser.disable()
                    }
                }
            }
        }
    }

    fun stop() {
        advertiser.disable()
        scanner.disable()
    }

    enum class Mode {
        BEST_EFFORT, CONNECT_ONLY
    }
}