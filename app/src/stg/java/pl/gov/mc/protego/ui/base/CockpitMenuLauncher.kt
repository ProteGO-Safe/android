package pl.gov.mc.protego.ui.base

import androidx.fragment.app.FragmentManager
import com.polidea.cockpit.cockpit.Cockpit
import timber.log.Timber

class CockpitMenuLauncher {
    companion object {
        fun showCockpit(fragmentManager: FragmentManager) {
           Cockpit.showCockpit(fragmentManager)
        }
    }
}