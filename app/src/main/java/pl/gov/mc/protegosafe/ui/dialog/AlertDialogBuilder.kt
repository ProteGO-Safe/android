package pl.gov.mc.protegosafe.ui.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import pl.gov.mc.protegosafe.R

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
}