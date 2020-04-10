package pl.gov.mc.protego.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.service.BluetoothServiceConnection
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.observeLiveData


class DashboardActivity : BaseActivity() {

    private val session: Session by inject()
    private val serviceConn: BluetoothServiceConnection by inject()
    override val viewModel: DashboardActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        observeLiveData(viewModel.dashboardPage) { changePage(it) }

        observeLiveData(viewModel.hasInternetConnection) { hasInternetConnection ->
            if (!hasInternetConnection)
                showNoInternetConnectionDialog()
            else
                hideNoInternetConnectionDialog()
        }

//        todo: kod do przeniesienia lub usunięcia
//        serviceConn.serviceConnectedData.observe(this, Observer { serviceConnected ->
//            service_toggle.isChecked = serviceConnected
//        })
//        service_toggle.setOnClickListener {
//            if (service_toggle.isChecked) {
//                serviceConn.startService()
//            } else {
//                serviceConn.stopService()
//            }
//        }
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
            viewModel.menuButtonPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (viewModel.dashboardPage.value is DashboardPage.HistoryPage) {
            viewModel.menuButtonPressed()
        } else {
            super.onBackPressed()
        }
    }

    private fun changePage(page: DashboardPage) {
        val fragmentToAdd: Fragment =
            supportFragmentManager.findFragmentByTag(page.pageFragmentTag) ?: page.createFragment()
        page.showFragment(supportFragmentManager, fragmentToAdd)
    }

    override fun observeIsInProgress() = Unit
}
