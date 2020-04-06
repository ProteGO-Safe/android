package pl.gov.mc.protego.ui.main

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.polidea.cockpit.cockpit.Cockpit
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.AppInformation
import pl.gov.mc.protego.ui.Event
import pl.gov.mc.protego.util.EmailClientAdapter

data class IntentToLaunch(
    val intent: Intent,
    @StringRes val errorMessage: Int
)

class HistoryViewModel(
    private val appInformation: AppInformation,
    private val resources: Resources,
    private val emailClientAdapter: EmailClientAdapter
) : ViewModel() {

    val versionLiveData = MutableLiveData<String>()
    val intentToStart = MutableLiveData<Event<IntentToLaunch>>()

    fun fetchData() {
        versionLiveData.value = appInformation.versionName
    }

    fun onContactClicked() {
        //TODO add copy for error and title
        intentToStart.value =
            Event(IntentToLaunch(
                emailClientAdapter.createIntent(resources.getString(R.string.dashboard_info_contact_us_email)),
                R.string.missing_email_client
            ))
    }

    fun onTermsAndConditionsClicked() {
        //TODO add copy for error
        intentToStart.value = Event(IntentToLaunch(
            Intent(Intent.ACTION_VIEW, Uri.parse(Cockpit.getTermsAndConditions())),
            R.string.missing_web_browser
        ))
    }
}
