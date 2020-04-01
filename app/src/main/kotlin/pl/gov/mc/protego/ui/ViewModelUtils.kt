package pl.gov.mc.protego.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <T> AppCompatActivity.observeLiveData(liveData: MutableLiveData<T>, observer: (T) -> Unit) {
    liveData.observe(this, Observer { observer(it) })
}