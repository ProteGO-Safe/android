package se.sigmaconnectivity.blescanner.data.db

import android.content.Context
import androidx.core.content.edit
import kotlin.reflect.KProperty

private const val SHARED_PREFS = "shared_prefs"

class SharedPreferencesDelegates(context: Context) {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    fun containsValue(prefKey: String) = sharedPreferences.contains(prefKey)

    fun booleanPref(prefKey: String, defaultValue: Boolean = false) = BooleanStorageDelegate(prefKey, defaultValue)

    fun stringPref(prefKey: String, defaultValue: String? = null) = StringStorageDelegate(prefKey, defaultValue)

    fun intPref(prefKey: String, defaultValue: Int) = IntStorageDelegate(prefKey, defaultValue)

    fun longPref(prefKey: String, defaultValue: Long = 0) = LongStorageDelegate(prefKey, defaultValue)

    fun stringSetPref(prefKey: String, defaultValue: Set<String> = emptySet()) = StringSetStorageDelegate(prefKey, defaultValue)

    fun remove(prefKey: String) {
        sharedPreferences.edit { remove(prefKey) }
    }

    inner class BooleanStorageDelegate(private val key: String, private val defaultValue: Boolean) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = sharedPreferences.getBoolean(key, defaultValue)

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) = sharedPreferences.edit { putBoolean(key, value) }
    }

    inner class StringStorageDelegate(private val key: String, private val defaultValue: String?) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String =
            sharedPreferences.getString(key, defaultValue) ?: throw IllegalStateException("The key: $key contains no value")

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) = sharedPreferences.edit { putString(key, value) }
    }

    inner class IntStorageDelegate(private val key: String, private val defaultValue: Int) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int =
            sharedPreferences.getInt(key, defaultValue)

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) = sharedPreferences.edit { putInt(key, value) }
    }

    inner class LongStorageDelegate(private val key: String, private val defaultValue: Long) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Long =
            sharedPreferences.getLong(key, defaultValue)

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) = sharedPreferences.edit { putLong(key, value) }
    }

    inner class StringSetStorageDelegate(private val key: String, private val defaultValue: Set<String>) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Set<String> = sharedPreferences.getStringSet(key, defaultValue)!!

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Set<String>) = sharedPreferences.edit { putStringSet(key, value) }
    }
}
