package pl.gov.mc.protegosafe.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.play.core.review.ReviewManagerFactory
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_home.webView
import kotlinx.android.synthetic.main.view_connection_error.view.button_cancel
import kotlinx.android.synthetic.main.view_connection_error.view.button_check_internet_connection
import kotlinx.android.synthetic.main.view_connection_error.view.text_view_connection_error
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protegosafe.BuildConfig
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.FragmentHomeBinding
import pl.gov.mc.protegosafe.domain.exception.CovidTestNotCompatibleDeviceException
import pl.gov.mc.protegosafe.domain.exception.UploadException
import pl.gov.mc.protegosafe.domain.model.ActivityRequest
import pl.gov.mc.protegosafe.domain.model.ActivityResult
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.model.SendSmsItem
import pl.gov.mc.protegosafe.domain.usecase.GetMigrationUrlUseCase
import pl.gov.mc.protegosafe.extension.toCompletable
import pl.gov.mc.protegosafe.extension.toSingle
import pl.gov.mc.protegosafe.logging.WebViewTimber
import pl.gov.mc.protegosafe.ui.common.BaseFragment
import pl.gov.mc.protegosafe.ui.common.livedata.observe
import timber.log.Timber

class HomeFragment : BaseFragment() {

    private val vm: HomeViewModel by viewModel()
    private val urlProvider by inject<WebUrlProvider>()
    private lateinit var binding: FragmentHomeBinding

    private var pwaDump: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        binding.vm = vm
        binding.lifecycleOwner = this

        setupPWA()
        observeRequests()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        manageWebView(resumed = true)
        vm.onAppLifecycleStateChanged(AppLifecycleState.RESUMED, webView.progress)
        vm.processPendingActivityResult()
    }

    override fun onPause() {
        vm.onAppLifecycleStateChanged(AppLifecycleState.PAUSED)
        manageWebView(resumed = false)
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("onActivityResult requestCode:$requestCode resultCode: $resultCode")
        val isResultOk = resultCode == RESULT_OK
        val activityRequest = ActivityRequest.valueOf(requestCode)
        vm.onActivityResult(ActivityResult(activityRequest, isResultOk))
    }

    private fun observeRequests() {
        vm.requestResolve.observe(viewLifecycleOwner, ::requestExposureNotificationPermission)
        vm.requestBluetooth.observe(viewLifecycleOwner, ::requestBluetooth)
        vm.requestLocation.observe(viewLifecycleOwner, ::requestLocation)
        vm.requestClearData.observe(viewLifecycleOwner, ::requestClearData)
        vm.requestNotifications.observe(viewLifecycleOwner, ::requestNotifications)
        vm.requestAppReview.observe(viewLifecycleOwner, ::startAppReview)
        vm.restartActivity.observe(viewLifecycleOwner, ::restartActivity)
        vm.closeApplication.observe(viewLifecycleOwner, ::closeApplication)
        vm.showConnectionError.observe(viewLifecycleOwner, ::showError)
        vm.openSmsApp.observe(viewLifecycleOwner, ::openSmsApp)
    }

    private fun setupPWA() {
        get<GetMigrationUrlUseCase>().execute()
            .subscribe(
                { url ->
                    if (url.isBlank()) {
                        setUpWebView()
                    } else {
                        startPwaMigration(url)
                    }
                },
                {
                    Timber.e(it, "Migration can not be performed")
                    setUpWebView()
                }
            ).addTo(disposables)
    }

    private fun startPwaMigration(url: String) {
        binding.migrationLayout.isVisible = true
        binding.webView.apply {
            applySettings()
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.webView.evaluateJavascript(DUMP_UI_COMMAND) { dump ->
                        pwaDump = dump
                        setUpWebView()
                    }
                }
            }
            loadUrl(url)
        }
    }

    private fun setUpWebView() {
        binding.webView.apply {
            applySettings()
            webViewClient = ProteGoWebViewClient()
            addJavascriptInterface(
                NativeBridgeInterface(
                    vm::setBridgeData,
                    vm::getBridgeData
                ),
                NativeBridgeInterface.NATIVE_BRIDGE_NAME
            )
            loadUrl(urlProvider.getWebUrl())
            if (BuildConfig.DEBUG) {
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        WebViewTimber.d("webView console ${consoleMessage.message()}")
                        return true
                    }
                }
            }
        }
        binding.webView.setOnLongClickListener {
            false
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    vm.onBackButtonPressed()
                }
            }
        )

        vm.javascriptCode.observe(viewLifecycleOwner, ::runJavascript)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun WebView.applySettings() {
        settings.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                forceDark = WebSettings.FORCE_DARK_OFF
            }
            javaScriptEnabled = true
            domStorageEnabled = true
        }
    }

    private inner class ProteGoWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return if (url.startsWith("tel:") ||
                url.startsWith("mailto:") ||
                !url.contains(urlProvider.getWebUrl())
            ) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                true
            } else false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            pwaDump?.let {
                loadDumpedPwaData(it)
                pwaDump = null
                binding.webView.reload()
                binding.migrationLayout.isVisible = false
            }
            vm.onPageFinished()
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.cancel()
            Timber.e(error.toString())
            binding.missingConnectionLayout.text_view_connection_error
                .setText(R.string.not_secure_connection_msg)
            binding.missingConnectionLayout.button_check_internet_connection.setOnClickListener {
                binding.webView.reload()
            }
            binding.webView.visibility = View.GONE
        }
    }

    private fun runJavascript(script: String) {
        WebViewTimber.d("run javascript: $script")
        binding.webView.evaluateJavascript(script, null)
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

    private fun requestBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, ActivityRequest.ENABLE_BLUETOOTH.requestCode)
    }

    private fun requestLocation() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(intent, ActivityRequest.ENABLE_LOCATION.requestCode)
    }

    private fun requestNotifications() {
        val packageName = activity?.packageName
        val settingsIntent: Intent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Settings.EXTRA_APP_PACKAGE, activity?.packageName)
                }
            } else {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    data = Uri.fromParts("package", packageName, null)
                }
            }
        startActivityForResult(settingsIntent, ActivityRequest.ENABLE_NOTIFICATIONS.requestCode)
    }

    private fun startAppReview() {
        val reviewManager = ReviewManagerFactory.create(context)

        reviewManager.requestReviewFlow().toSingle()
            .flatMapCompletable {
                reviewManager.launchReviewFlow(activity, it).toCompletable()
            }.subscribe(
                {
                    if (BuildConfig.BUILD_TYPE != Consts.RELEASE_BUILD_TYPE) {
                        Toast.makeText(context, "App reviewed", Toast.LENGTH_LONG).show()
                    }
                },
                {
                    Timber.e(it, "App review failed")
                }
            ).addTo(disposables)
    }

    private fun openSmsApp(sendSmsItem: SendSmsItem) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:${sendSmsItem.number}")
            putExtra("sms_body", sendSmsItem.text)
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun restartActivity() {
        activity?.recreate()
    }

    private fun closeApplication() {
        activity?.finish()
    }

    private fun showError(error: Exception) {
        if (error !is CovidTestNotCompatibleDeviceException) {
            binding.missingConnectionLayout.button_check_internet_connection.visibility =
                View.VISIBLE
            binding.missingConnectionLayout.button_check_internet_connection.setOnClickListener {
                binding.webView.visibility = View.VISIBLE
                vm.onRequestRetry()
            }
        } else {
            binding.missingConnectionLayout.button_check_internet_connection.visibility =
                View.INVISIBLE
        }

        binding.missingConnectionLayout.button_cancel.setOnClickListener {
            vm.onRequestCanceled()
            binding.webView.visibility = View.VISIBLE
        }
        binding.missingConnectionLayout.text_view_connection_error.setText(
            getErrorText(error)
        )
        binding.webView.visibility = View.INVISIBLE
    }

    private fun requestClearData() {
        binding.webView.evaluateJavascript(CLEAR_UI_COMMAND) {
            openExposureNotificationSettings()
        }
    }

    private fun openExposureNotificationSettings() {
        startActivityForResult(
            Intent(ACTION_EXPOSURE_NOTIFICATION_SETTINGS),
            ActivityRequest.CLEAR_EXPOSURE_NOTIFICATION_DATA.requestCode
        )
    }

    private fun manageWebView(resumed: Boolean) {
        Timber.d("manageWebView, resumed: $resumed")
        if (resumed) {
            binding.webView.onResume()
            binding.webView.resumeTimers()
        } else {
            binding.webView.onPause()
            binding.webView.pauseTimers()
        }
    }

    private fun getErrorText(error: Exception): Int {
        return when (error) {
            is UploadException.GetTemporaryExposureKeysError -> {
                R.string.get_temporary_exposure_keys_error
            }
            is UploadException.PinVerificationError -> {
                R.string.pin_verification_error
            }
            is UploadException.UploadTemporaryExposureKeysError -> {
                R.string.upload_temporary_exposure_keys_error
            }
            is UploadException.NoKeysError -> {
                R.string.upload_temporary_exposure_keys_empty_error
            }
            is UploadException.TotalLimitExceededError -> {
                R.string.upload_temporary_exposure_keys_limit_exceed_error
            }
            is UploadException.DailyLimitExceededError -> {
                R.string.upload_temporary_exposure_keys_day_limit_exceed_error
            }
            is CovidTestNotCompatibleDeviceException -> {
                R.string.covid_test_not_compatible_device
            }
            else -> {
                R.string.no_internet_connection_msg
            }
        }
    }

    private fun loadDumpedPwaData(dump: String) {
        val script = "localStorage.setItem(\"persist:root\",$dump);"
        runJavascript(script)
    }
}

private const val ACTION_EXPOSURE_NOTIFICATION_SETTINGS =
    "com.google.android.gms.settings.EXPOSURE_NOTIFICATION_SETTINGS"
private const val DUMP_UI_COMMAND = "localStorage.getItem(\"persist:root\");"
private const val CLEAR_UI_COMMAND = "localStorage.clear()"
