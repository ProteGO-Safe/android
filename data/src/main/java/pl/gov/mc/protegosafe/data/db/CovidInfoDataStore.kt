package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_COVID_INFO_VOIVODESHIPS_UPDATE_TIMESTAMP =
    "data.db.CovidInfoDataStore-covid-info-voivodeships-update-timestamp"

private const val SHARED_PREFS_COVID_INFO_COVID_STATS_CHECK_TIMESTAMP =
    "data.db.CovidInfoDataStore-covid-info-covid-stats-check-timestamp"

class CovidInfoDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var voivodeshipsUpdateTimestamp by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_COVID_INFO_VOIVODESHIPS_UPDATE_TIMESTAMP, 0L
    )

    var covidStatsCheckTimestamp by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_COVID_INFO_COVID_STATS_CHECK_TIMESTAMP, 0L
    )
}
