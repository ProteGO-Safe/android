package pl.gov.mc.protego.ui.base

import android.content.ActivityNotFoundException
import android.widget.Toast
import kotlinx.android.synthetic.main.toolbar.*
import pl.gov.mc.protego.ui.observeLiveData

abstract class BaseActivity<T> : BaseVariantActivity() {

    protected abstract val viewModel: BaseViewModel<T>

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
}