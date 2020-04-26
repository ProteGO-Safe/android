package pl.gov.mc.protegosafe.repository

import android.content.Context
import android.widget.Toast
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.domain.repository.ToastRepository

class ToastRepositoryImpl(private val context: Context) : ToastRepository {
    override fun showIsBtServiceEnabledInfo(isEnabled: Boolean) {
        Toast.makeText(
            context,
            if (isEnabled) {
                R.string.bluetooth_module_enabled
            } else {
                R.string.bluetooth_module_disabled
            },
            Toast.LENGTH_SHORT
        ).show()
    }
}