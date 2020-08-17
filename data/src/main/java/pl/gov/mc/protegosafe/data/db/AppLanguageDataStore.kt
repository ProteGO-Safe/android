package pl.gov.mc.protegosafe.data.db

import java.util.Locale

private const val SHARED_PREFS_APP_LANG_ISO =
    "data.db.AppLanguageDataStore-app-lang-iso"

class AppLanguageDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var appLanguageISO by sharedPreferencesDelegates.stringPref(
        SHARED_PREFS_APP_LANG_ISO, Locale.getDefault().language.toUpperCase(Locale.getDefault())
    )
}
