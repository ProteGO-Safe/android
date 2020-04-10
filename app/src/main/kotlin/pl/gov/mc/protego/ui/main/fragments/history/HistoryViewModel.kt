package pl.gov.mc.protego.ui.main.fragments.history

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.AppInformation
import pl.gov.mc.protego.ui.Event
import pl.gov.mc.protego.ui.IntentToLaunch
import pl.gov.mc.protego.ui.TermsAndConditionsIntentCreator
import pl.gov.mc.protego.ui.base.BaseViewModel
import pl.gov.mc.protego.ui.put
import pl.gov.mc.protego.util.EmailClientAdapter


class HistoryViewModel(
    private val appInformation: AppInformation,
    private val resources: Resources,
    private val emailClientAdapter: EmailClientAdapter,
    private val termsAndConditionsIntentCreator: TermsAndConditionsIntentCreator
) : BaseViewModel() {

    private val _versionLiveData = MutableLiveData<String>()
    val versionLiveData: LiveData<String>
        get() = _versionLiveData

    fun fetchData() {
        _versionLiveData.value = appInformation.versionName
    }

    fun onContactClicked() {
        _intentToStart.value =
            Event(
                IntentToLaunch(
                    emailClientAdapter.createIntent(resources.getString(R.string.dashboard_info_contact_us_email)),
                    R.string.missing_email_client
                )
            )
    }

    fun onTermsAndConditionsClicked() =
        _intentToStart put termsAndConditionsIntentCreator.intentToLaunch.wrapInEvent()
}
