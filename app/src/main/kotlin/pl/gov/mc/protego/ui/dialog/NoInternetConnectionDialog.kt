package pl.gov.mc.protego.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.no_internet_connection_dialog.*
import pl.gov.mc.protego.R

class NoInternetConnectionDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = true
        return inflater.inflate(R.layout.no_internet_connection_dialog, container, false)
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            go_to_internet_settings.setOnClickListener {
                try {
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                } catch (throwable: Throwable) {
                    Toast.makeText(context, R.string.cannot_start_wireless_config, Toast.LENGTH_LONG).show()
                    dismiss()
                }
            }
    }
}
