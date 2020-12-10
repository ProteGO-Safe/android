package pl.gov.mc.protegosafe.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.ActivityTestHelpersBinding
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.model.ResolutionRequest
import pl.gov.mc.protegosafe.logging.WebViewTimber
import pl.gov.mc.protegosafe.ui.common.livedata.observe
import timber.log.Timber

class TestHelpersActivity() : AppCompatActivity(), KoinComponent {

    private val viewModel: TestHelpersViewModel by viewModel()
    private lateinit var binding: ActivityTestHelpersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_helpers)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        initObservers()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ResolutionRequest.ACCESS_TEMPORARY_EXPOSURE_KEYS.code &&
            resultCode == RESULT_OK
        ) {
            viewModel.shareTemporaryExposureKeys()
        }
    }

    private fun initObservers() {
        viewModel.successfulEvent.observe(lifecycleOwner = this, block = ::handleSuccessEvent)
        viewModel.failedEvent.observe(lifecycleOwner = this, block = ::handleFailedEvent)
        viewModel.requestResolve.observe(
            lifecycleOwner = this,
            block = ::requestExposureNotificationPermission
        )
        viewModel.shareTemporaryExposureKeys.observe(lifecycleOwner = this, block = ::shareText)
    }

    private fun requestExposureNotificationPermission(
        exception: ExposureNotificationActionNotResolvedException
    ) {
        WebViewTimber
            .d("Request exposure notification permission: ${exception.resolutionRequest}")
        when (val apiException = exception.apiException) {
            is ApiException -> {
                startIntentSenderForResult(
                    apiException.status.resolution.intentSender,
                    exception.resolutionRequest.code,
                    null,
                    0,
                    0,
                    0,
                    null
                )
            }
            else -> Timber.e(exception, "Not supported exception")
        }
    }

    private fun shareText(text: String) {
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }.let {
            startActivity(it)
        }
    }

    private fun handleSuccessEvent(info: String) {
        Toast.makeText(this, "$info set", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun handleFailedEvent(info: String) {
        Toast.makeText(this, info, Toast.LENGTH_LONG).show()
    }
}
