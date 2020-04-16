package pl.gov.mc.protegosafe.data.db

private const val SHARED_PREFS_NOTIF_DATA = "notif-data"
class NotificationDataStore(private val sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var notifData by sharedPreferencesDelegates.stringPref(SHARED_PREFS_NOTIF_DATA, "")
}