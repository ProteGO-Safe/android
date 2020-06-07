package pl.gov.mc.protegosafe.ui.common

import android.content.Context
import androidx.appcompat.app.AlertDialog
import pl.gov.mc.protegosafe.R

fun getSafetyNetErrorAlertDialog(
    context: Context
): AlertDialog? {
    return AlertDialog.Builder(context)
        .setTitle(R.string.safetynet_dialog_verification_error)
        .setMessage(R.string.safetynet_dialog_verification_error)
        .setNeutralButton(context.getString(R.string.common_ok), null)
        .setCancelable(true)
        .create()
}
