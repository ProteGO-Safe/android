package pl.gov.mc.protego.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.gov.mc.protego.ui.Event
import pl.gov.mc.protego.ui.IntentToLaunch


abstract class BaseViewModel<T> : ViewModel() {

    protected val _intentToStart = MutableLiveData<Event<IntentToLaunch>>()
    val intentToStart: LiveData<Event<IntentToLaunch>>
        get() = _intentToStart

    protected val _isInProgress = MutableLiveData<T>()
    val isInProgress: LiveData<T>
        get() = _isInProgress
}