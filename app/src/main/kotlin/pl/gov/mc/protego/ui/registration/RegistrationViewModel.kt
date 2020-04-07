package pl.gov.mc.protego.ui.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protego.backend.domain.ProtegoServer
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.information.SessionData
import pl.gov.mc.protego.ui.validator.MsisdnInvalid
import pl.gov.mc.protego.ui.validator.MsisdnOk
import pl.gov.mc.protego.ui.validator.MsisdnValidationResult
import pl.gov.mc.protego.ui.validator.MsisdnValidator
import timber.log.Timber

class RegistrationViewModel(
    private val msisdnValidator: MsisdnValidator,
    private val protegoServer: ProtegoServer,
    private val session: Session
)  : ViewModel() {

    val msisdnError = MutableLiveData<MsisdnValidationResult>()
    val sessionData = MutableLiveData<SessionData>()

    private var disposables = CompositeDisposable()


    fun fetchSession() {
        sessionData.value = session.sessionData
    }

    fun onNewMsisdn(msisdn: String) {
        msisdnError.value = msisdnValidator.validate(msisdn)
    }

    fun onStartRegistration(msisdn: String) {
        protegoServer
            .initRegistration(msisdn)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { sessionData.value = it }
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
}