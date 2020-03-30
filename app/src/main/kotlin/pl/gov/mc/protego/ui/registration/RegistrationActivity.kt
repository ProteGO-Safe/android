package pl.gov.mc.protego.ui.registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.registration_view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import pl.gov.mc.protego.information.SessionState
import pl.gov.mc.protego.ui.main.MainActivity


class RegistrationActivity : AppCompatActivity() {

    private val registrationViewModel: RegistrationViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_view)

        register_button.setOnClickListener {
            registrationViewModel.onStartRegistration(msisdn_edit_text.text.toString())
        }

        msisdn_edit_text.onTextChanged(registrationViewModel::onNewMsisdn)

        observeMsisdnValidation()
        observeRegistrationStatus()

        registrationViewModel.fetchSession()
    }

    private fun observeRegistrationStatus() {
        registrationViewModel.sessionData.addObserver {
            if (it.state == SessionState.REGISTRATION) {
                Toast.makeText(this, "Verification code: $it", Toast.LENGTH_LONG).show()
                navigateToConfirmation()
            }
            when(it.state) {
                SessionState.REGISTRATION -> navigateToConfirmation().also { Toast.makeText(this, "Verification code: $it.code", Toast.LENGTH_LONG).show() }
                SessionState.LOGGED_IN -> navigateToMain()
            }
        }
    }

    private fun observeMsisdnValidation() {
        registrationViewModel.msisdnError.addObserver {
            msisdn_edit_text_layout.error = it
        }
    }

    private fun navigateToConfirmation() {
        startActivity(Intent(this, RegistrationConfirmationActivity::class.java))
        finish()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun <T> MutableLiveData<T> .addObserver(observer: (T) -> Unit) {
        observe(this@RegistrationActivity, Observer { observer(it) })
    }

    private fun TextInputEditText.onTextChanged(onChange: (String) -> Unit) {
        addTextChangedListener(ValidationTextWatcher(onChange))
    }

    private inner class ValidationTextWatcher(val validator: (String) -> Unit) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun afterTextChanged(editable: Editable) {
            validator(editable.toString())
        }
    }
}