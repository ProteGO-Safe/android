package pl.gov.mc.protego.ui.base

import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.toolbar.*
import pl.gov.mc.protego.ui.dialog.NoInternetConnectionDialog


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

    private val noInternetConnectionDialog: DialogFragment
        get() = supportFragmentManager.findFragmentByTag(NO_INTERNET_DIALOG) as DialogFragment?
            ?: NoInternetConnectionDialog()

    fun showNoInternetConnectionDialog() {
        noInternetConnectionDialog.apply {
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