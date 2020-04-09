package pl.gov.mc.protego.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protego.backend.domain.ProtegoServer
import pl.gov.mc.protego.ui.base.BaseViewModel
import timber.log.Timber

class DashboardActivityViewModel(
    private val protegoServer: ProtegoServer
) : BaseViewModel() {

    private val _dashboardPage = MutableLiveData<DashboardPage>().apply {
        value = DashboardPage.MainPage()
    }
    val dashboardPage: LiveData<DashboardPage>
        get() = _dashboardPage

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

    fun menuButtonPressed() {
        _dashboardPage.value = when (_dashboardPage.value) {
            is DashboardPage.MainPage -> DashboardPage.HistoryPage()
            else -> DashboardPage.MainPage()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}