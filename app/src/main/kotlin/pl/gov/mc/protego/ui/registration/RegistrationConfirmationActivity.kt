package pl.gov.mc.protego.ui.registration

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.android.synthetic.main.registration_confirmation_view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.receivers.SMSBroadcastReceiver
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.main.DashboardActivity
import pl.gov.mc.protego.ui.observeLiveData
import pl.gov.mc.protego.ui.scrollWhenFocusObtained

class RegistrationConfirmationActivity : BaseActivity() {

    override val viewModel: RegistrationConfirmationViewModel by viewModel()
    private val smsBroadcastReceiver: SMSBroadcastReceiver by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_confirmation_view)

        sms_code.doOnTextChanged { text, _, _, _ -> viewModel.onCodeChanged(text.toString()) }

        confirm_registration_button.setOnClickListener {
            viewModel.confirm(sms_code.text.toString())
        }
        sms_code.scrollWhenFocusObtained(scroll_view)

        setupLinkToTermsOfUse()

        with(viewModel) {
            observeLiveData(confirmationEnabled) { confirm_registration_button.isEnabled = it }

            observeLiveData(confirmationError) {
                Toast.makeText(
                    this@RegistrationConfirmationActivity,
                    "Problem z rejestracją: $it",
                    Toast.LENGTH_LONG
                ).show()
            }

            observeLiveData(confirmationSuccess) {
                navigateToMain()
            }
        }
        observeIntents()
        observeIsInProgress()
        SmsRetriever.getClient(this).startSmsRetriever()
        smsBroadcastReceiver.smsContent.observe(this, Observer {
            if (it == null) return@Observer
            sms_code.setText(it)
        })
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(
            smsBroadcastReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION,
            null
        )
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsBroadcastReceiver)
    }

    override fun observeIsInProgress() {
        observeLiveData(viewModel.isInProgress) {
            sms_code.isEnabled = !it
            confirm_registration_button.isEnabled = !it
        }
    }

    override fun onDestroy() {
        sms_code.onFocusChangeListener = null
        super.onDestroy()
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

    private fun navigateToMain() {
        startActivity(Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
