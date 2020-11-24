package pl.gov.mc.protegosafe.logging

import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.data.db.WebViewLoggingDataStore
import timber.log.Timber

object WebViewTimber : Timber.Tree(), KoinComponent {
    private val webViewLoggingDataStore: WebViewLoggingDataStore by inject()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (webViewLoggingDataStore.isLoggingEnabled) {
            when {
                t != null -> {
                    Timber.e(t, message)
                }
                else -> {
                    Timber.d(message)
                }
            }
        }
    }
}
