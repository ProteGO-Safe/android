package pl.gov.mc.protego.ui.base

import androidx.fragment.app.FragmentManager
import timber.log.Timber

class CockpitMenuLauncher {
    companion object {
        fun showCockpit(supportFragmentManager: FragmentManager) {
            Timber.i("Cockpit menu is not available for Release.")
        }
    }
}