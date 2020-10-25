package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_COVID_INFO_UPDATE_TIMESTAMP =
    "data.db.CovidInfoDataStore-covid-info-update-timestamp"

class CovidInfoDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var updateTimestamp by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_COVID_INFO_UPDATE_TIMESTAMP, 0L
    )
}
