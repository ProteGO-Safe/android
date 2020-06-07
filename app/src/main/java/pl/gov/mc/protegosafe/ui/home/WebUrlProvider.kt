package pl.gov.mc.protegosafe.ui.home

import pl.gov.mc.protegosafe.BuildConfig
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates

const val WEB_URL = "web_url_key"

class WebUrlProvider(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    private var _webUrl by sharedPreferencesDelegates.stringPref(WEB_URL, "")

    fun getWebUrl() =
        if (BuildConfig.DEBUG) {
            if (_webUrl.isEmpty()) {
                _webUrl = BuildConfig.WEB
            }
            _webUrl
        } else {
            BuildConfig.WEB
        }
}
