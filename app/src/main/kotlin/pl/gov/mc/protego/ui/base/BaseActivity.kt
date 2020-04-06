package pl.gov.mc.protego.ui.base

import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseActivity : BaseVariantActivity() {

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        toolbar.apply {
            setSupportActionBar(this)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
            }
        }
    }
}