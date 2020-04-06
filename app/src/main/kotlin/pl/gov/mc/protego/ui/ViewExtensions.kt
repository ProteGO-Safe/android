package pl.gov.mc.protego.ui

import android.view.View
import android.widget.EditText
import android.widget.ScrollView

fun ScrollView.scrollToBottom() {
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY + height)
    smoothScrollBy(0, delta)
}

fun EditText.scrollWhenFocusObtained(view: ScrollView) {
    this.onFocusChangeListener =
        View.OnFocusChangeListener { _, hasFocus ->
            if(hasFocus) view.postDelayed({
                view.scrollToBottom()
            }, 200)
        }
}
