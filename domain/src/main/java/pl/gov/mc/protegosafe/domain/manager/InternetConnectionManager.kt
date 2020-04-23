package pl.gov.mc.protegosafe.domain.manager

interface InternetConnectionManager {
    enum class InternetConnectionStatus {
        NONE,
        MOBILE_DATA,
        WIFI;

        fun isConnected(): Boolean {
            return this in arrayListOf(MOBILE_DATA, WIFI)
        }
    }

    fun getInternetConnectionStatus(): InternetConnectionStatus
}