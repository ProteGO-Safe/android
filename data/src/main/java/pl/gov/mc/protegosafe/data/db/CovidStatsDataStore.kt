package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_COVID_STATS_NOTIFICATIONS_AGREEMENT =
    "data.db.AppLanguageDataStore-covid_stats_notifications_agreement"

class CovidStatsDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var covidStatsNotificationsAgreement by sharedPreferencesDelegates.booleanPref(
        SHARED_PREFS_COVID_STATS_NOTIFICATIONS_AGREEMENT, true
    )
}
