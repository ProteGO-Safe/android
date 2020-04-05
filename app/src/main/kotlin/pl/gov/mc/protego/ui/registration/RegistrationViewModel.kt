package pl.gov.mc.protego.ui.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protego.backend.domain.ProtegoServer
import pl.gov.mc.protego.information.PhoneInformation
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.information.SessionData
import pl.gov.mc.protego.ui.validator.MsisdnValidator
import timber.log.Timber

class RegistrationViewModel(
    private val msisdnValidator: MsisdnValidator,
    private val protegoServer: ProtegoServer,
    private val session: Session,
    private val phoneInformation: PhoneInformation
)  : ViewModel() {

    val msisdnError = MutableLiveData<String?>()
    val sessionData = MutableLiveData<SessionData>()
    val noInternetConnection = MutableLiveData<Boolean>()

    private var disposables = CompositeDisposable()

    fun onResume() {
        fetchSession()
        val hasActiveInternetConnection = phoneInformation.hasActiveInternetConnection
        noInternetConnection.value = hasActiveInternetConnection
    }

    private fun fetchSession() {
        sessionData.value = session.sessionData
    }

    fun onNewMsisdn(msisdn: String) {
        if (!msisdnValidator.validate(msisdn)) {
            msisdnError.value = "Niepoprawny numer telefonu"
        } else {
            msisdnError.value = null
        }
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