package pl.gov.mc.protego.bluetooth.advertiser


interface AdvertiserListener {

    /**
     * Method to be called when [AdvertiserInterface] encounters error and disables itself
     * @param advertiserInterface the [AdvertiserInterface] that encountered error
     * @param advertiserError the [AdvertiserError] that caused it
     */
    fun error(advertiserInterface: AdvertiserInterface, advertiserError: AdvertiserError)

    sealed class AdvertiserError {
        data class Advertiser(val errorCode: Int) : AdvertiserError()
        data class Server(val status: Int) : AdvertiserError()
    }
}