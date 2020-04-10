package pl.gov.mc.protego.bluetooth

import android.os.Handler
import android.os.Looper
import timber.log.Timber


@Suppress("NOTHING_TO_INLINE") // so the log will show in proper place
inline fun safeCurrentThreadHandler(): Handler {
    val hopefullyCurrentLooper = Looper.myLooper() ?: Looper.getMainLooper().also {
        Timber.w("Cannot obtain current thread looper")
    }
    return Handler(hopefullyCurrentLooper)
}