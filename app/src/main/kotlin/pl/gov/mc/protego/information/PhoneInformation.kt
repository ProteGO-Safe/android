package pl.gov.mc.protego.information

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.wifi.WifiManager
import java.util.*


class PhoneInformation(
    val context: Context
) {
    val lang: String
        get() = Locale.getDefault().language

    val deviceName: String
        get() = "${android.os.Build.MANUFACTURER} ${android.os.Build.PRODUCT} ${android.os.Build.MODEL}"

    val osVersion: String
        get() = android.os.Build.VERSION.SDK_INT.toString()

    val platform: String
        get() = "android"

    val hasActiveInternetConnection: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mobileDataEnabled =  connectivityManager.allNetworks.any {
                connectivityManager.getNetworkCapabilities(it)?.hasTransport(TRANSPORT_CELLULAR) == true
            }

            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager?

            return wifiManager?.isWifiEnabled ?: false || mobileDataEnabled
        }
}