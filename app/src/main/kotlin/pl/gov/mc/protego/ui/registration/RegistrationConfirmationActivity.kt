package pl.gov.mc.protego.ui.registration

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.registration_confirmation_view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.main.DashboardActivity
import pl.gov.mc.protego.ui.observeLiveData

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

        setupLinkToTermsOfUse()

        observeLiveData(viewModel.confirmationError) {
            Toast.makeText(this, "Problem z rejestracją: $it", Toast.LENGTH_LONG).show()
        }

        observeLiveData(viewModel.confirmationSuccess) {
            navigateToMain()
        }
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
                            //TODO link to terms of use
                            Toast.makeText(
                                this@RegistrationConfirmationActivity,
                                "Nie ma jeszcze regulaminu",
                                Toast.LENGTH_SHORT
                            ).show()
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
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
