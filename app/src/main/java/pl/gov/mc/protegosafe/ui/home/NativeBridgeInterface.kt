package pl.gov.mc.protegosafe.ui.home

import android.webkit.JavascriptInterface
import timber.log.Timber


class NativeBridgeInterface(
    private val setBridgeDataCallback: ((dataType: Int, data: String) -> Unit),
    private val getBridgeDataCallback: (dataType: Int) -> String
) {

    @JavascriptInterface
    fun setBridgeData(dataType: Int, data: String) {
        Timber.d("setBridgeData: $dataType - $data")
        setBridgeDataCallback(dataType, data)
    }

    @JavascriptInterface
    fun getBridgeData(dataType: Int): String {
        Timber.d("getBridgeData called: $dataType")
        return getBridgeDataCallback(dataType)
    }

    companion object {
        const val NATIVE_BRIDGE_NAME = "NativeBridge"
    }
}