package pl.gov.mc.protegosafe.ui.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.manager.SafetyNetManager

object AlertDialogBuilder {
    fun getInternetConnectionAlertDialog(
        context: Context,
        onClickListener: DialogInterface.OnClickListener
    ): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle(R.string.no_internet_connection_title)
            .setMessage(context.getString(R.string.no_internet_connection_msg))
            .setPositiveButton(
                context.getString(R.string.try_again),
                onClickListener
            )
            .setCancelable(false)
            .create()
    }

    fun getSafetyNetErrorAlertDialog(
        context: Context,
        result: SafetyNetManager.SafetyNetResult,
        onClickListener: DialogInterface.OnClickListener
    ): AlertDialog? {
        val message = when(result) {
            is SafetyNetManager.SafetyNetResult.Failure.UpdatePlayServicesError ->
                context.getString(R.string.safetynet_dialog_verification_error_play_services)
            is SafetyNetManager.SafetyNetResult.Failure.SafetyError ->
                context.getString(R.string.safetynet_dialog_verification_error)
            is SafetyNetManager.SafetyNetResult.Failure.ConnectionError ->
                context.getString(R.string.safetynet_dialog_verification_error_internet_connection)
            is SafetyNetManager.SafetyNetResult.Failure.UnknownError ->
                context.getString(R.string.safetynet_dialog_verification_unknown_error)
            else -> null
        }

        return AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.safetynet_dialog_title))
            .setMessage(message)
            .setPositiveButton(
                context.getString(R.string.try_again),
                onClickListener
            )
            .setCancelable(false)
            .create()
    }
}