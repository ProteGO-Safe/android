package pl.gov.mc.protego.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import pl.gov.mc.protego.util.SmsParser
import timber.log.Timber

class SMSBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras ?: return
            val status = extras[SmsRetriever.EXTRA_STATUS] as Status
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message: String? = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    //TODO uzupełnianie kodu w view
                    Timber.d(SmsParser.getCodeFromSms(message!!))
                }
            }
        }
    }
}