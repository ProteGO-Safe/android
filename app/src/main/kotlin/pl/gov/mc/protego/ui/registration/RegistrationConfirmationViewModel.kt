package pl.gov.mc.protego.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.polidea.cockpit.cockpit.Cockpit
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protego.backend.domain.ProtegoServer
import pl.gov.mc.protego.ui.TermsAndConditionsIntentCreator
import pl.gov.mc.protego.ui.base.BaseViewModel
import pl.gov.mc.protego.ui.put
import timber.log.Timber

class RegistrationConfirmationViewModel(
    private val protegoServer: ProtegoServer,
    private val termsAndConditionsIntentCreator: TermsAndConditionsIntentCreator
) : BaseViewModel() {

    private var disposables = CompositeDisposable()

    private val _confirmationError = MutableLiveData<String>()
    val confirmationError: LiveData<String>
        get() = _confirmationError

    private val _confirmationSuccess = MutableLiveData<Boolean>()
    val confirmationSuccess: LiveData<Boolean>
        get() = _confirmationSuccess

    private val _confirmationEnabled = MutableLiveData(false)
    val confirmationEnabled: LiveData<Boolean>
        get() = _confirmationEnabled

    fun confirm(code: String) {
        protegoServer
            .confirmRegistration(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    Timber.e(it, "Confirmation error: ${it.message}")
                    _confirmationError.value = it.message
                },
                onSuccess = {
                    Timber.d("Confirmed")
                    _confirmationSuccess.value = true
                }
            )
            .addTo(disposables)
    }

    fun onCodeChanged(code: String) {
        _confirmationEnabled.value = code.length == Cockpit.getSmsCodeLength()
    }

    fun onTermsAndConditionsClicked() =
        _intentToStart put termsAndConditionsIntentCreator.intentToLaunch.wrapInEvent()
}