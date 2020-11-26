package pl.gov.mc.protegosafe.device.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager.InternetConnectionStatus

class InternetConnectionManagerImpl(private val context: Context) : InternetConnectionManager {

    @Suppress("DEPRECATION")
    override fun getInternetConnectionStatus(): InternetConnectionStatus {
        var result = InternetConnectionStatus.NONE

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.run {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            result = InternetConnectionStatus.WIFI
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            result = InternetConnectionStatus.MOBILE_DATA
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                            result = InternetConnectionStatus.VPN
                        }
                    }
                }
            }
        } else {
            connectivityManager?.run {
                connectivityManager.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = InternetConnectionStatus.WIFI
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = InternetConnectionStatus.MOBILE_DATA
                    }
                }
            }
        }
        return result
    }
}
