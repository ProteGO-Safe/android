package pl.gov.mc.protegosafe.data.extension

import android.content.SharedPreferences

@Suppress("UNCHECKED_CAST")
fun SharedPreferences.copyTo(to: SharedPreferences) {
    val prefsData = all
    if (prefsData.isNotEmpty()) {
        val editor = to.edit()
        prefsData.forEach { (key, value) ->
            when (value) {
                is Int -> editor.putInt(key, value)
                is Float -> editor.putFloat(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is String -> editor.putString(key, value)
                is Long -> editor.putLong(key, value)
                is Set<*> -> editor.putStringSet(key, value as Set<String>)
            }
        }
        editor.apply()
    }
}
