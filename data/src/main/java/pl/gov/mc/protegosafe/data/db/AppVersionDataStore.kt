package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_LAST_VERSION_URL =
    "data.db.AppVersionDataStore-last-version-url"
private const val SHARED_PREFS_CURRENT_VERSION_NAME =
    "data.db.AppVersionDataStore-current-version-name"

class AppVersionDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {
    var lastVersionUrl: String by sharedPreferencesDelegates.stringPref(
        SHARED_PREFS_LAST_VERSION_URL, ""
    )
    var currentVersionName: String by sharedPreferencesDelegates.stringPref(
        SHARED_PREFS_CURRENT_VERSION_NAME, ""
    )
}
