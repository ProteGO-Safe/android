package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_COVID_INFO_VERSION =
    "data.db.CovidInfoDataStore-covid-info-version"

private const val SHARED_PREFS_COVID_INFO_DASHBOARD_UPDATE_TIMESTAMP =
    "data.db.CovidInfoDataStore-covid-info-dashboard-update-timestamp"

private const val SHARED_PREFS_COVID_INFO_DETAILS_UPDATE_TIMESTAMP =
    "data.db.CovidInfoDataStore-covid-info-details-update-timestamp"

private const val SHARED_PREFS_COVID_INFO_VOIVODESHIPS_UPDATE_TIMESTAMP =
    "data.db.CovidInfoDataStore-covid-info-voivodeships-update-timestamp"

class CovidInfoDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var currentVersion by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_COVID_INFO_VERSION, 1L
    )

    var dashboardUpdateTimestamp by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_COVID_INFO_DASHBOARD_UPDATE_TIMESTAMP, 0L
    )

    var detailsUpdateTimestamp by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_COVID_INFO_DETAILS_UPDATE_TIMESTAMP, 0L
    )

    var voivodeshipsUpdateTimestamp by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_COVID_INFO_VOIVODESHIPS_UPDATE_TIMESTAMP, 0L
    )
}
