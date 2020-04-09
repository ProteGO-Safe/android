package pl.gov.mc.protego.ui.base

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.toolbar.*
import pl.gov.mc.protego.ui.observeLiveData
import pl.gov.mc.protego.ui.dialog.NoInternetConnectionDialog


abstract class BaseActivity : BaseVariantActivity() {

    protected abstract val viewModel: BaseViewModel

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        toolbar.apply {
            setSupportActionBar(this)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
            }
        }
    }

    protected fun observeIntents() {
        observeLiveData(viewModel.intentToStart) {
            it.getContentIfNotHandled()
                ?.also {
                    try {
                        startActivity(it.intent)
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(
                            this@BaseActivity,
                            it.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            //TODO add conditional finish of this activity
        }
    }

    protected abstract fun observeIsInProgress()

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