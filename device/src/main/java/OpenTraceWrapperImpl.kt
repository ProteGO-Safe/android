package pl.gov.mc.protegosafe.data

import android.content.Context
import io.bluetrace.opentrace.Utils
import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.OpenTraceWrapper

class OpenTraceWrapperImpl(private val context: Context) : OpenTraceWrapper {
    fun startService() =
        Completable.fromAction {
            Utils.startBluetoothMonitoringService(context)
        }
}