package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_COVID_STATS_NOTIFICATION_AGREEMENT =
    "data.db.AppLanguageDataStore-covid_stats_notification_agreement"

class CovidStatsDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var covidStatsNotificationAgreement by sharedPreferencesDelegates.booleanPref(
        SHARED_PREFS_COVID_STATS_NOTIFICATION_AGREEMENT, true
    )
}
