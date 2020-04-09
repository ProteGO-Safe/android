package pl.gov.mc.protego.ui

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import com.polidea.cockpit.cockpit.Cockpit
import pl.gov.mc.protego.R

data class IntentToLaunch(
    val intent: Intent,
    @StringRes val errorMessage: Int
) {
    fun wrapInEvent(): Event<IntentToLaunch> = Event(this)
}

interface IntentCreator {
    val intentToLaunch: IntentToLaunch
}

//TODO: move all navigation here, ie. navigateToMain/Dashboard, Registration, etc.

class TermsAndConditionsIntentCreator : IntentCreator {
    override val intentToLaunch: IntentToLaunch
        get() = IntentToLaunch(
            Intent(Intent.ACTION_VIEW, Uri.parse(Cockpit.getTermsAndConditions())),
            R.string.missing_web_browser
        )
}
