package pl.gov.mc.protegosafe.ui.common

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.reactivestreams.Publisher


open class BaseViewModel : ViewModel() {

    protected val disposables = CompositeDisposable()

    protected fun Disposable.track() {
        disposables.add(this)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    protected fun <T> MediatorLiveData<T>.add(publisher: Publisher<T>) {
        addSource(LiveDataReactiveStreams.fromPublisher(publisher)) {
            postValue(it)
        }
    }
}