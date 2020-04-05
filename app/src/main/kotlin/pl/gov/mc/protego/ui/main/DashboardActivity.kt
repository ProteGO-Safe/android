package pl.gov.mc.protego.ui.main

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.observeLiveData
import pl.gov.mc.protego.ui.registration.onboarding.OnboardingActivity

class DashboardActivity : BaseActivity() {

    private val viewModel: DashboardActivityViewModel by viewModel()
    private val session: Session by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        logout_button.setOnClickListener {
            session.logout()
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
        }
        observeLiveData(viewModel.noInternetConnection) { hasInternetConnection ->
            if (!hasInternetConnection)
                showNoInternetConnectionDialog()
            else
                hideNoInternetConnectionDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }
}
