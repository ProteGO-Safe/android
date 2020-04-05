package pl.gov.mc.protego.bluetooth.scanner


interface ScannerInterface {

    enum class Mode {
        SCAN_ONLY, SCAN_AND_EXCHANGE_BEACON_IDS
    }

    fun enable(inMode: Mode)

    fun disable()
}