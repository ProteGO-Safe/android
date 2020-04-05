package pl.gov.mc.protego.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protego.backend.domain.ProtegoServer
import pl.gov.mc.protego.information.PhoneInformation
import timber.log.Timber

class DashboardActivityViewModel(
    private val protegoServer: ProtegoServer,
    private val phoneInformation: PhoneInformation
) : ViewModel() {

    private var disposables = CompositeDisposable()
    val noInternetConnection = MutableLiveData<Boolean>()

    fun onResume() {
        protegoServer
            .fetchState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { Timber.e(it, "Fetch State Error") },
                onSuccess = { Timber.d("State fetched") }
            )
            .addTo(disposables)

        noInternetConnection.value = phoneInformation.hasActiveInternetConnection
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}