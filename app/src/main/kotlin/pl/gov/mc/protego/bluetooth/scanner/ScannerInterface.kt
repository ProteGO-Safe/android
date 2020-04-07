package pl.gov.mc.protego.bluetooth.scanner


interface ScannerInterface {

    enum class Mode {
        SCAN_ONLY, SCAN_AND_EXCHANGE_BEACON_IDS
    }

    /**
     * Method that enables the [ScannerInterface] in particular mode
     * @param inMode the [Mode] that [ScannerInterface] should work with
     * @param listener
     */
    fun enable(inMode: Mode, listener: ScannerListener)

    /**
     * Method that disables the [ScannerInterface]
     */
    fun disable()
}