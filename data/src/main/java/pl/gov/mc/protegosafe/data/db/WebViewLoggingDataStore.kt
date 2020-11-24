package pl.gov.mc.protegosafe.data.db

import pl.gov.mc.protegosafe.data.BuildConfig

private const val SHARED_PREFS_IS_WEB_VIEW_LOGGING_ENABLED =
    "data.db.LoggingDataStore-web-view-logging-enabled"

class WebViewLoggingDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var isLoggingEnabled by sharedPreferencesDelegates.booleanPref(
        SHARED_PREFS_IS_WEB_VIEW_LOGGING_ENABLED, BuildConfig.ENABLE_WEBVIEW_LOGS
    )
}
