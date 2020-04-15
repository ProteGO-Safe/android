package se.sigmaconnectivity.blescanner.data.db

private const val SHARED_PREFS_HASH = "hash"

class UserIdStore(private val sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var userHash by sharedPreferencesDelegates.stringPref(SHARED_PREFS_HASH, "")
}