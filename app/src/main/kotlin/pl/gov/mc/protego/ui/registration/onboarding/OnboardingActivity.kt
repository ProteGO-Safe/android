package pl.gov.mc.protego.ui.registration.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.registration.RegistrationActivity

class OnboardingActivity : BaseActivity() {

    private val viewModel: OnboardingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        with(viewModel) {
            page.observe(this@OnboardingActivity, Observer { showPage(it) })
            navigateToRegistration.observe(this@OnboardingActivity, Observer { navigateToRegistration() })
        }
        back_button.setOnClickListener { viewModel.onBackClicked() }
        next_button.setOnClickListener { viewModel.onNextClicked() }
    }

    private fun showPage(pageInfo: PageInfo) {
        scrolling_container.scrollTo(0, 0)
        illustration.setImageResource(pageInfo.illustration)
        page_title.displayIfNotNull(pageInfo.title)
        header.displayIfNotNull(pageInfo.header)
        description.setText(pageInfo.description)
        back_button.visible(pageInfo.backButtonVisible)
        next_button.visible(pageInfo.nextButtonVisible)
    }

    private fun navigateToRegistration() =
        startActivity(Intent(this, RegistrationActivity::class.java))
}

private fun TextView.displayIfNotNull(@StringRes textId: Int?) {
    if (textId == null) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        setText(textId)
    }
}

private fun View.visible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}