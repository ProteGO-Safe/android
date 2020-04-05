package pl.gov.mc.protego.ui.base

import android.os.Bundle
import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseActivity : BaseVariantActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.apply {
            setSupportActionBar(this)

            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
            }
        }
    }
}