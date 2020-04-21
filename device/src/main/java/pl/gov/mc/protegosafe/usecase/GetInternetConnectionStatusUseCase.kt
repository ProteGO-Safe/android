package pl.gov.mc.protegosafe.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import pl.gov.mc.protegosafe.domain.usecase.IGetInternetConnectionStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.IGetInternetConnectionStatusUseCase.InternetConnectionStatus


class GetInternetConnectionStatusUseCase(private val context: Context) :
    IGetInternetConnectionStatusUseCase {

    override fun execute(): InternetConnectionStatus {
        var result = InternetConnectionStatus.NONE

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.run {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = InternetConnectionStatus.WIFI
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = InternetConnectionStatus.MOBILE_DATA
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