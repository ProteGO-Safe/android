package pl.gov.mc.protego.ui.registration

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.polidea.cockpit.cockpit.Cockpit
import kotlinx.android.synthetic.main.registration_view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.SessionState
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.main.DashboardActivity
import pl.gov.mc.protego.ui.observeLiveData
import pl.gov.mc.protego.ui.scrollWhenFocusObtained
import timber.log.Timber


class RegistrationActivity : BaseActivity() {

    override val viewModel: RegistrationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_view)

        register_button.isEnabled = false
        register_button.setOnClickListener {
            viewModel.onStartRegistration(msisdn_edit_text.text.toString().replace(" ", ""))
        }

        skip_registration_button.setOnClickListener {
            viewModel.onSkipRegistrationClicked()
        }

        msisdn_edit_text.onTextChanged(viewModel::onNewMsisdn)
        msisdn_edit_text.scrollWhenFocusObtained(scroll_view)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        setupLinkToTermsOfUse()
        observeMsisdnValidation()
        observeRegistrationStatus()
        observeIntents()
        observeIsInProgress()

        observeLiveData(viewModel._hasInternetConnection) { hasInternetConnection ->
            if (!hasInternetConnection) {
                Timber.d("Show no internet dialog")
                showNoInternetConnectionDialog()
            } else {
                Timber.d("Hide no internet dialog")
                hideNoInternetConnectionDialog()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onDestroy() {
        msisdn_edit_text.onFocusChangeListener = null
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun observeIsInProgress() {
        observeLiveData(viewModel.isInProgress) {
            msisdn_edit_text_layout.isEnabled = !it
            skip_registration_button.isEnabled = !it
            register_button.isEnabled = !it
        }
    }

    private fun observeRegistrationStatus() {
        observeLiveData(viewModel.sessionData) { sessionData ->
            when (sessionData.state) {
                SessionState.REGISTRATION -> navigateToConfirmation().also {
                    if (!Cockpit.isSendSmsDuringRegistration())
                        Toast.makeText(
                            this,
                            "kod weryfikacyjny: ${sessionData.debugCode}",
                            Toast.LENGTH_LONG
                        ).show()
                }
                SessionState.LOGGED_IN -> navigateToMain()
            }
        }
    }

    private fun observeMsisdnValidation() {
        observeLiveData(viewModel.msisdnError) {
            register_button.isEnabled = it.success
            msisdn_edit_text_layout.error =
                if (it.errorMessage != null) getString(it.errorMessage) else null
        }
    }

    private fun navigateToConfirmation() {
        startActivity(Intent(this, RegistrationConfirmationActivity::class.java))
    }

    private fun navigateToMain() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun setupLinkToTermsOfUse() {
        accept_tou.apply {
            val nonClickablePart = getString(
                R.string.registration_terms_of_use_btn_regular_part
            ).substringBefore("%")
            val clickablePart = getString(R.string.registration_terms_of_use_btn_underlined_part)
            text = SpannableString("$nonClickablePart$clickablePart").apply {
                setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            viewModel.onTermsAndConditionsClicked()
                        }
                    },
                    nonClickablePart.length,
                    nonClickablePart.length + clickablePart.length,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }
            movementMethod = LinkMovementMethod()
        }
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
