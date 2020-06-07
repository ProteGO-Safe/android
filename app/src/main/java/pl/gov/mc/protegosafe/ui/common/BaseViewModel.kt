package pl.gov.mc.protegosafe.ui.common

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    protected fun Disposable.track() {
        disposables.add(this)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
