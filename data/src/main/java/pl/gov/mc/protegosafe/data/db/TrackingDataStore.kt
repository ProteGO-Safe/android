package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_TRACK_AGREEMENT_KEY = "data.db.TrackingDataStore-tracking-agreement"

class TrackingDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {
    var isTrackingAccepted by sharedPreferencesDelegates.booleanPref(
        SHARED_PREFS_TRACK_AGREEMENT_KEY
    )
}