package pl.gov.mc.protegosafe.domain.usecase

interface IGetInternetConnectionStatusUseCase {
    enum class InternetConnectionStatus {
        NONE,
        MOBILE_DATA,
        WIFI;

        fun isConnected() : Boolean {
            return this in arrayListOf(MOBILE_DATA, WIFI)
        }
    }

    fun execute() : InternetConnectionStatus
}