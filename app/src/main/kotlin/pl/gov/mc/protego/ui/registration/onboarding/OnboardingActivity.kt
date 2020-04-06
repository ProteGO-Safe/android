package pl.gov.mc.protego.ui.registration.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_onboarding.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.observeLiveData
import pl.gov.mc.protego.ui.registration.RegistrationActivity

class OnboardingActivity : BaseActivity() {

    private val viewModel: OnboardingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        with(viewModel) {
            observeLiveData(page, ::showPage)
            observeLiveData(navigateToRegistration) { navigateToRegistration() }
            observeLiveData(finishApplication) { finish() }
        }
        back_button.setOnClickListener { viewModel.onBackClicked() }
        next_button.setOnClickListener { viewModel.onNextClicked() }
    }

    override fun onBackPressed() {
        viewModel.onBackClicked()
    }

    private fun showPage(pageInfo: PageInfo) {
        scrolling_container.scrollTo(0, 0)
        illustration.setImageResource(pageInfo.illustration)
        page_title.displayIfNotNull(pageInfo.title)
        header.displayIfNotNull(pageInfo.header)
        description.setText(pageInfo.description)
        back_button.isVisible = pageInfo.backButtonVisible
        next_button.isVisible = pageInfo.nextButtonVisible
        status_legend.isVisible = pageInfo.statusLegendVisible
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
