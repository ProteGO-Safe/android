package pl.gov.mc.protegosafe.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.missing_connection.view.button_check_internet_connection
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protegosafe.R
import pl.gov.mc.protegosafe.databinding.FragmentHomeBinding
import pl.gov.mc.protegosafe.ui.common.BaseFragment


class HomeFragment : BaseFragment() {

    private val vm: HomeViewModel by viewModel()
    private val urlProvider by inject<WebUrlProvider>()
    private lateinit var binding: FragmentHomeBinding

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

        return binding.root
    }

    private fun setListeners() {
        binding.missingConnectionLayout.button_check_internet_connection.setOnClickListener {
            updateWebViewVisibility()
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
            updateWebViewVisibility()
            super.onReceivedError(view, request, error)
        }
    }

    private fun updateWebViewVisibility() {
        binding.webView.visibility =
            if (vm.isInternetConnectionAvailable()) View.VISIBLE else View.GONE
    }
}
