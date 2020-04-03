package pl.gov.mc.protego.ui.main

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.registration.RegistrationActivity

class DashboardActivity : BaseActivity() {

    private val viewModel: DashboardActivityViewModel by viewModel()
    private val session: Session by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        logout_button.setOnClickListener {
            session.logout()
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }
}
