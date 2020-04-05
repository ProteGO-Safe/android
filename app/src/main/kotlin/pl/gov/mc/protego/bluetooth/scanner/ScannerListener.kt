package pl.gov.mc.protego.bluetooth.scanner


interface ScannerListener {

    /**
     * Method to be called when the [ScannerInterface] encounters error and disables itself
     * @param scannerInterface the [ScannerInterface] that encountered error
     * @param throwable the error that happened
     */
    fun error(scannerInterface: ScannerInterface, throwable: Throwable)
}