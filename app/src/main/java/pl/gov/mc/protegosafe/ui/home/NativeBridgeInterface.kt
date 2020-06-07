package pl.gov.mc.protegosafe.ui.home

import android.webkit.JavascriptInterface
import pl.gov.mc.protegosafe.logging.webViewTimber

class NativeBridgeInterface(
    private val setBridgeDataCallback: ((dataType: Int, data: String) -> Unit),
    private val getBridgeDataCallback: ((dataType: Int, data: String, requestId: String) -> Unit)
) {

    @JavascriptInterface
    fun setBridgeData(dataType: Int, data: String) {
        webViewTimber().d("setBridgeData: $dataType - $data")
        setBridgeDataCallback(dataType, data)
    }

    @JavascriptInterface
    fun setBridgeData(dataType: Int) {
        webViewTimber().d("setBridgeData: $dataType")
        setBridgeDataCallback(dataType, "")
    }

    @JavascriptInterface
    fun getBridgeData(dataType: Int, data: String, requestId: String) {
        webViewTimber().d("getBridgeData called: $dataType")
        getBridgeDataCallback(dataType, data, requestId)
    }

    companion object {
        const val NATIVE_BRIDGE_NAME = "NativeBridge"
    }
}
