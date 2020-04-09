package pl.gov.mc.protego.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> AppCompatActivity.observeLiveData(liveData: LiveData<T>, observer: (T) -> Unit) {
    liveData.observe(this, Observer { observer(it) })
}

fun <T> Fragment.observeLiveData(liveData: LiveData<T>, observer: (T) -> Unit) {
    liveData.observe(this.viewLifecycleOwner, Observer { observer(it) })
}

infix fun <T> MutableLiveData<T>.put(event: T) {
    value = event
}

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

enum class UiLock {
    NO_LOCK, DISABLE_INPUTS, SHOW_ONLY_SPINNER
}