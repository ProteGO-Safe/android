package pl.gov.mc.protego.ui.registration

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protego.backend.domain.ProtegoServer
import timber.log.Timber

class RegistrationConfirmationViewModel(
    private val protegoServer: ProtegoServer
) : ViewModel() {

    private var disposables = CompositeDisposable()

    val confirmationError = MutableLiveData<String>()
    val confirmationSuccess = MutableLiveData<Boolean>()

    fun confirm(code: String) {
        protegoServer
            .confirmRegistration(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    Timber.e(it, "Confirmation error: ${it.message}")
                    confirmationError.value = it.message
                },
                onSuccess = {
                    Timber.d("Confirmed")
                    confirmationSuccess.value = true
                }
            )
            .addTo(disposables)
    }
}