package pl.gov.mc.protego.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.registration_confirmation_view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.main.DashboardActivity
import pl.gov.mc.protego.ui.observeLiveData
import pl.gov.mc.protego.ui.scrollWhenFocusObtained

class RegistrationConfirmationActivity : BaseActivity() {

    private val viewModel: RegistrationConfirmationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_confirmation_view)

        confirm_registration_button.setOnClickListener {
            if (sms_code.text.toString().isNotEmpty()) {
                viewModel.confirm(sms_code.text.toString())
            } else {
                sms_code_layout.error = "Wpisz kod"
            }
        }
        sms_code.scrollWhenFocusObtained(scroll_view)

        observeLiveData(viewModel.confirmationError) {
            Toast.makeText(this, "Problem z rejestracją: $it", Toast.LENGTH_LONG).show()
        }

        observeLiveData(viewModel.confirmationSuccess) {
            navigateToMain()
        }
    }

    override fun onDestroy() {
        sms_code.onFocusChangeListener = null
        super.onDestroy()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
