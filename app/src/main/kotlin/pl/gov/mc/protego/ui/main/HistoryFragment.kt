package pl.gov.mc.protego.ui.main

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.history_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.graphics.Paint.UNDERLINE_TEXT_FLAG
import android.graphics.Paint
import com.polidea.cockpit.cockpit.Cockpit
import pl.gov.mc.protego.ui.observeLiveData
import timber.log.Timber


class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        observeVersion()
        observeIntents()

        addUnderlineToLinks()

        rules.setOnClickListener { viewModel.onTermsAndConditionsClicked() }
        e_mail.setOnClickListener { viewModel.onContactClicked() }
        viewModel.fetchData()
    }

    private fun addUnderlineToLinks() {
        rules.paintFlags = rules.paintFlags or UNDERLINE_TEXT_FLAG
        e_mail.paintFlags = e_mail.paintFlags or UNDERLINE_TEXT_FLAG
    }

    private fun observeVersion() {
        observeLiveData(viewModel.versionLiveData) { app_version.text = it }
    }

    private fun observeIntents() {
        observeLiveData(viewModel.intentToStart) {
            it.getContentIfNotHandled()
            ?.also {
                try {
                    startActivity(it.intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(
                        this@HistoryFragment.context,
                        it.errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
