package se.sigmaconnectivity.blescanner.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_dialog.*
import se.sigmaconnectivity.blescanner.R

class InfoDialogFragment : DialogFragment() {
    private val navArgs by navArgs<InfoDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        messageText.setText(navArgs.messageTextRes)
        setupOkButton()
    }

    private fun setupOkButton() {
        okButton.setOnClickListener {
            dismiss()
        }
    }
}