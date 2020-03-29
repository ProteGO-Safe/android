package pl.gov.anna.ui.registration

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.anna.backend.domain.AnnaServer
import timber.log.Timber

class RegistrationConfirmationViewModel(
    private val annaServer: AnnaServer
) : ViewModel() {

    private var disposables = CompositeDisposable()

    val confirmationError = MutableLiveData<String>()
    val confirmationSuccess = MutableLiveData<Boolean>()

    fun confirm(code: String) {
        annaServer
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