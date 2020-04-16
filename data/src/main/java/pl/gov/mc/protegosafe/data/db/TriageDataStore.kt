package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_TRIAGE_TIMESTAMP = "triage-completed-timestamp"

class TriageDataStore(private val sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var lastTriageCompletedTimestamp by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_TRIAGE_TIMESTAMP
    )
}