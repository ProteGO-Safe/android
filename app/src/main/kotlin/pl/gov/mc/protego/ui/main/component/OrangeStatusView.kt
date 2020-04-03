package pl.gov.mc.protego.ui.main.component

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import pl.gov.mc.protego.R

class OrangeStatusView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    init {
        inflate(context, R.layout.orange_status, this)
    }
}