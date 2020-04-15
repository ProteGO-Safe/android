package se.sigmaconnectivity.blescanner.ui.common.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, block: () -> Unit) {
    observe(lifecycleOwner, Observer { block() })
}

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, block: (T) -> Unit) {
    observe(lifecycleOwner, Observer { block(it) })
}

fun <T> LiveData<T>.requireValue() = value ?: throw IllegalStateException("LiveData contains no value")

fun <T> LiveData<T?>.requireValueFromNullableData(): T = value ?: throw IllegalStateException("LiveData contains no value")

fun <T> asyncLiveDataOf(item: T): LiveData<T> = MutableLiveData<T>().apply { postValue(item) }

fun <T> LiveData<T?>.filterNotNull(): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        if (it != null) {
            mutableLiveData.value = it
        }
    }
    return mutableLiveData
}