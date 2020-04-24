package pl.gov.mc.protegosafe.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.missing_connection.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.FragmentHomeBinding
import pl.gov.mc.protegosafe.ui.common.BaseFragment
import pl.gov.mc.protegosafe.ui.common.livedata.observe
import timber.log.Timber


class HomeFragment : BaseFragment() {

    private val vm: HomeViewModel by viewModel()
    private val urlProvider by inject<WebUrlProvider>()
    private lateinit var binding: FragmentHomeBinding
    private val rxperm by lazy {
        RxPermissions(this)
    }
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        setListeners()
        setUpWebView()
        observeRequests()
        return binding.root
    }

    private fun setListeners() {
        binding.missingConnectionLayout.button_check_internet_connection.setOnClickListener {
            binding.webView.reload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == RESULT_OK) {
                    vm.onBluetoothEnable()
                }
            }
            REQUEST_POWER_SETTINGS -> {
                vm.onPowerSettingsResult()
            }
            else -> Unit
        }
    }

    private fun observeRequests() {
        vm.requestPermissions.observe(viewLifecycleOwner, ::openRequestPermissions)
        vm.requestBluetooth.observe(viewLifecycleOwner, ::requestBluetooth)
        vm.changeBatteryOptimization.observe(viewLifecycleOwner, ::openPowerSettings)
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
            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                    Timber.d("webView console ${consoleMessage.message()}")
                    return true
                }
            }
        }
        binding.webView.setOnLongClickListener {
            false
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        activity?.finish() //TODO handle back normally by activity
                    }
                }
            })

        vm.javascriptCode.observe(viewLifecycleOwner, ::runJavascript)
        vm.webViewVisibilityChanged.observe(viewLifecycleOwner, ::setWebViewVisible)
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

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            vm.onWebViewReceivedError(request, error)
            super.onReceivedError(view, request, error)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            vm.onWebViewPageFinished(url)
            super.onPageFinished(view, url)
        }
    }

    private fun setWebViewVisible(visible: Boolean) {
        if (binding.webView.isVisible != visible) {
            binding.webView.isVisible = visible
        }
    }

    private fun runJavascript(script: String) {
        Timber.d("run javascript: $script")
        binding.webView.evaluateJavascript(script, null)
    }

    private fun openRequestPermissions() {
        rxperm.request(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe({
                Timber.d("Permissions accepted")
                vm.onPermissionsAccepted()
            }, {
                Timber.d("Permissions rejected")
            }).addTo(compositeDisposable)
    }

    private fun requestBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    private fun openPowerSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val openPowerSettings = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            startActivityForResult(openPowerSettings, REQUEST_POWER_SETTINGS)
        }
    }
}

private const val REQUEST_ENABLE_BT = 1
private const val REQUEST_POWER_SETTINGS = 2