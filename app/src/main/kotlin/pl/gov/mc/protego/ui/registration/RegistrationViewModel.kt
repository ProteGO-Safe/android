package pl.gov.mc.protego.ui.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protego.backend.domain.ProtegoServer
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.information.SessionData
import pl.gov.mc.protego.ui.base.BaseViewModel
import pl.gov.mc.protego.ui.validator.MsisdnValidationResult
import pl.gov.mc.protego.ui.validator.MsisdnValidator
import timber.log.Timber

class RegistrationViewModel(
    private val msisdnValidator: MsisdnValidator,
    private val protegoServer: ProtegoServer,
    private val session: Session
)  : BaseViewModel() {

    private val _msisdnError = MutableLiveData<MsisdnValidationResult>()
    val msisdnError: LiveData<MsisdnValidationResult>
            get() = _msisdnError
    private val _sessionData = MutableLiveData<SessionData>()
    val sessionData: LiveData<SessionData>
        get() = _sessionData

    private val disposables = CompositeDisposable()


    fun fetchSession() {
        _sessionData.value = session.sessionData
    }

    fun onNewMsisdn(msisdn: String) {
        _msisdnError.value = msisdnValidator.validate(msisdn.replace(" ", ""))
    }

    fun onStartRegistration(msisdn: String) {
        protegoServer
            .registerWithPhoneNumber(msisdn)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { _sessionData.value = it }
            .subscribeBy(
                onError = { Timber.e(it, "Registration Error") },
                onSuccess = { Timber.d("Registration request succeed") }
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun onTermsAndConditionsClicked() = navigateToTermsAndConditions()

    fun onSkipRegistrationClicked() {
        protegoServer
            .registerAnonymously()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { _sessionData.value = it }
            .subscribeBy(
                onError = { Timber.e(it, "Anonymous registration Error") },
                onSuccess = { Timber.d("Anonymous registration request succeed") }
            ).addTo(disposables)
    }
}