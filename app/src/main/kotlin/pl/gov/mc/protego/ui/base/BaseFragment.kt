package pl.gov.mc.protego.ui.base

import android.content.ActivityNotFoundException
import android.widget.Toast
import androidx.fragment.app.Fragment
import pl.gov.mc.protego.ui.observeLiveData

abstract class BaseFragment : Fragment() {
    abstract val viewModel: BaseViewModel

    protected fun observeIntents() {
        observeLiveData(viewModel.intentToStart) {
            it.getContentIfNotHandled()
                ?.also {
                    try {
                        startActivity(it.intent)
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(
                            this@BaseFragment.context,
                            it.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}