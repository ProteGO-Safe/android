package io.bluetrace.opentrace.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.webview.*
import io.bluetrace.opentrace.BuildConfig
import io.bluetrace.opentrace.R
import io.bluetrace.opentrace.logging.CentralLog

class WebViewActivity : FragmentActivity() {

    private val TAG = "WebViewActivity"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra("url") ?: BuildConfig.MAIN_URL

        setContentView(R.layout.webview)
        webview.apply {
            webViewClient = WebViewClient()
            loadUrl(url)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }

        val wbc: WebChromeClient = object : WebChromeClient() {
            override fun onCloseWindow(w: WebView) {
                CentralLog.d(TAG, "OnCloseWindow for WebChromeClient")
            }
        }

        webview.webChromeClient = wbc
    }
}
