package pl.gov.mc.protegosafe.data.db

const val SHARED_PREFS_SAFETYNET_IS_DEVICE_CHECKED =
    "data.db.SafetyNetDataStore-safetynet-is-device-checked"

class SafetyNetDataStore(sharedPreferencesDelegates: SharedPreferencesDelegates) {
    var isDeviceChecked: Boolean by sharedPreferencesDelegates.booleanPref(
        SHARED_PREFS_SAFETYNET_IS_DEVICE_CHECKED, false
    )
}
