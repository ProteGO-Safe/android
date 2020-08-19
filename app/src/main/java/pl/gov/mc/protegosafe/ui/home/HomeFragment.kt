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
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.view_connection_error.view.button_cancel
import kotlinx.android.synthetic.main.view_connection_error.view.button_check_internet_connection
import kotlinx.android.synthetic.main.view_connection_error.view.text_view_connection_error
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protegosafe.BuildConfig
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.FragmentHomeBinding
import pl.gov.mc.protegosafe.domain.exception.UploadException
import pl.gov.mc.protegosafe.domain.model.ActivityRequest
import pl.gov.mc.protegosafe.domain.model.ActivityResult
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.usecase.GetMigrationUrlUseCase
import pl.gov.mc.protegosafe.logging.webViewTimber
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
    ): View? {
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
        vm.onAppLifecycleStateChanged(AppLifecycleState.RESUMED)
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
        vm.restartActivity.observe(viewLifecycleOwner, ::restartActivity)
        vm.showConnectionError.observe(viewLifecycleOwner, ::showConnectionError)
    }

    private fun setupPWA() {
        get<GetMigrationUrlUseCase>().execute()
            .subscribe({ url ->
                if (url.isBlank()) {
                    setUpWebView()
                } else {
                    startPwaMigration(url)
                }
            }, {
                Timber.e(it, "Migration can not be performed")
                setUpWebView()
            }).addTo(disposables)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startPwaMigration(url: String) {
        binding.migrationLayout.isVisible = true
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.webView.evaluateJavascript(DUMP_PWA_SCRIPT) { dump ->
                        pwaDump = dump
                        setUpWebView()
                    }
                }
            }
            loadUrl(url)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = ProteGoWebViewClient()
            addJavascriptInterface(
                NativeBridgeInterface(
                    vm::setBridgeData,
                    vm::getBridgeData
                ), NativeBridgeInterface.NATIVE_BRIDGE_NAME
            )
            loadUrl(urlProvider.getWebUrl())
            if (BuildConfig.DEBUG) {
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                        webViewTimber().d("webView console ${consoleMessage.message()}")
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
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        activity?.finish()
                    }
                }
            })

        vm.javascriptCode.observe(viewLifecycleOwner, ::runJavascript)
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
        webViewTimber().d("run javascript: $script")
        binding.webView.evaluateJavascript(script, null)
    }

    private fun requestExposureNotificationPermission(exception: ExposureNotificationActionNotResolvedException) {
        webViewTimber().d("Request exposure notification permission: ${exception.resolutionRequest}")
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

    private fun restartActivity() {
        activity?.recreate()
    }

    private fun showConnectionError(error: Exception) {
        binding.missingConnectionLayout.button_check_internet_connection.setOnClickListener {
            binding.webView.visibility = View.VISIBLE
            vm.onUploadRetry()
        }
        binding.missingConnectionLayout.button_cancel.setOnClickListener {
            vm.sendUploadCanceled()
            binding.webView.visibility = View.VISIBLE
        }
        binding.missingConnectionLayout.text_view_connection_error.setText(
            getConnectionErrorText(error)
        )
        binding.webView.visibility = View.INVISIBLE
    }

    private fun requestClearData() {
        val intent = Intent(ACTION_EXPOSURE_NOTIFICATION_SETTINGS)
        startActivityForResult(
            intent,
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

    private fun getConnectionErrorText(error: Exception): Int {
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
private const val DUMP_PWA_SCRIPT = "localStorage.getItem(\"persist:root\");"
