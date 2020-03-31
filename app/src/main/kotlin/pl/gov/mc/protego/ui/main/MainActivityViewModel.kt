package pl.gov.mc.protego.ui.main

import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protego.backend.domain.ProtegoServer
import timber.log.Timber

class MainActivityViewModel(
    val protegoServer: ProtegoServer
) : ViewModel() {

    private var disposables = CompositeDisposable()

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
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}