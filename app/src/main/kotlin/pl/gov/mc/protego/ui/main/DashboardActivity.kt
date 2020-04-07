package pl.gov.mc.protego.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.service.BluetoothServiceConnection
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.registration.onboarding.OnboardingActivity
import android.view.Menu
import android.view.MenuItem


class DashboardActivity : BaseActivity() {

    private val viewModel: DashboardActivityViewModel by viewModel()
    private val session: Session by inject()
    private val serviceConn: BluetoothServiceConnection by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        logout_button.setOnClickListener {
            serviceConn.stopService()
            session.logout()
            startActivity(Intent(this, OnboardingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }

        serviceConn.serviceConnectedData.observe(this, Observer { serviceConnected ->
            service_toggle.isChecked = serviceConnected
        })
        service_toggle.setOnClickListener {
            if (service_toggle.isChecked) {
                serviceConn.startService()
            } else {
                serviceConn.stopService()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        serviceConn.bindService()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onStop() {
        serviceConn.unbindService()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_menu) {
            val fragment = supportFragmentManager.findFragmentByTag(HISTORY_PANEL_TAG)
            if (fragment != null  && fragment is HistoryFragment) {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right)
                    .remove(fragment)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right)
                    .add(R.id.container, HistoryFragment(), HISTORY_PANEL_TAG)
                    .commit()
            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val HISTORY_PANEL_TAG = "histPanel"
    }
}
