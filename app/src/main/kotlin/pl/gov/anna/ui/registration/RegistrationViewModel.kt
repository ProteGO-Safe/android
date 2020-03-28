package pl.gov.anna.ui.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.anna.backend.domain.AnnaServer
import pl.gov.anna.ui.validator.MsisdnValidator
import timber.log.Timber

class RegistrationViewModel(
    private val msisdnValidator: MsisdnValidator,
    private val annaServer: AnnaServer
)  : ViewModel() {

    val msisdnError = MutableLiveData<String?>()
    val registrationCode = MutableLiveData<String>()

    private var disposables = CompositeDisposable()

    fun onNewMsisdn(msisdn: String) {
        if (!msisdnValidator.validate(msisdn)) {
            msisdnError.value = "Niepoprawny numer telefonu"
        } else {
            msisdnError.value = null
        }
    }

    fun onStartRegistration(msisdn: String) {
        annaServer
            .startRegistration(msisdn)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { if(it.code != null) registrationCode.value = it.code }
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