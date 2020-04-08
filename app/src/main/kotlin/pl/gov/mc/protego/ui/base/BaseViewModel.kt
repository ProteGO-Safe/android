package pl.gov.mc.protego.ui.base

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polidea.cockpit.cockpit.Cockpit
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.Event


data class IntentToLaunch(
    val intent: Intent,
    @StringRes val errorMessage: Int
)

abstract class BaseViewModel : ViewModel() {

    protected val _intentToStart = MutableLiveData<Event<IntentToLaunch>>()
    val intentToStart: LiveData<Event<IntentToLaunch>>
        get() = _intentToStart

    protected val _isInProgress = MutableLiveData(false)
    val isInProgress: LiveData<Boolean>
        get() = _isInProgress


    //TODO: move all navigation here, ie. navigateToMain/Dashboard, Registration, etc.
    protected fun navigateToTermsAndConditions() {
        _intentToStart.value = Event(
            IntentToLaunch(
                Intent(Intent.ACTION_VIEW, Uri.parse(Cockpit.getTermsAndConditions())),
                R.string.missing_web_browser
            )
        )
    }
}