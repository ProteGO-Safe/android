package pl.gov.mc.protegosafe.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_loading.view.*
import pl.gov.mc.protegosafe.R

class LoadingDialog : DialogFragment() {
    companion object {
        const val TAG = "pl.gov.mc.protegosafe.ui.dialog.LoadingDialogTag"
        private const val KEY_MESSAGE = "message"

        fun newInstance(message: String): LoadingDialog {
            return LoadingDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_MESSAGE, message)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_loading, container, false).apply {
            arguments?.getString(KEY_MESSAGE)?.let {
                loading_text_view.text = it
            }
        }
    }

    override fun dismiss() {
        if (isAdded) {
            super.dismiss()
        }
    }
}