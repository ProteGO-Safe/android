package pl.gov.mc.protegosafe.logging

import pl.gov.mc.protegosafe.BuildConfig
import timber.log.Timber

class NoLoggingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) = Unit
}

fun webViewTimber(): Timber.Tree {
    return if (BuildConfig.ENABLE_WEBVIEW_LOGS) {
        Timber.asTree()
    } else {
        NoLoggingTree()
    }
}
