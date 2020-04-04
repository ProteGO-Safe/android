package pl.gov.mc.protego.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseActivity : AppCompatActivity() {

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
