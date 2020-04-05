package pl.gov.mc.protego.ui.base

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import org.koin.android.ext.android.inject
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.dialog.NoInternetConnectionDialog
import kotlinx.android.synthetic.main.toolbar.*


abstract class BaseActivity : AppCompatActivity() {
    private val shakeDetector: CockpitShakeDetector by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
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

    fun showNoInternetConnectionDialog() {
        supportFragmentManager.findFragmentByTag(NO_INTERNET_DIALOG) ?: NoInternetConnectionDialog().apply {
            show(supportFragmentManager, NO_INTERNET_DIALOG)
        }
    }

    fun hideNoInternetConnectionDialog() {
        supportFragmentManager.findFragmentByTag(NO_INTERNET_DIALOG)?.also {
            (it as DialogFragment).dismiss()
        }
    }

    companion object {
        private const val NO_INTERNET_DIALOG = "NO_INTERNET_DIALOG"
    }
}