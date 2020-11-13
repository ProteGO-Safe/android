package pl.gov.mc.protegosafe.data.db

import pl.gov.mc.protegosafe.data.Consts

private const val SHARED_PREFS_REPEAT_INTERVAL_IN_MINUTES =
    "data.db.WorkersIntervalDataStore-workers-repeat-interval"

class WorkersIntervalDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {

    var timeIntervalInMinutes by sharedPreferencesDelegates.longPref(
        SHARED_PREFS_REPEAT_INTERVAL_IN_MINUTES, Consts.WORKERS_INTERVAL_IN_MINUTES
    )
}
