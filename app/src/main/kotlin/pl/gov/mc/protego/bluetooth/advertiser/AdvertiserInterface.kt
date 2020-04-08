package pl.gov.mc.protego.bluetooth.advertiser


interface AdvertiserInterface {

    /**
     * Function that enables the [AdvertiserInterface]
     * @param listener [AdvertiserListener] to be used until [disable] is called
     * @return the result of enabling
     */
    fun enable(listener: AdvertiserListener): EnableResult

    /**
     * Method that disables the [AdvertiserInterface] and stops reporting to [AdvertiserListener] set via [enable]
     */
    fun disable()
}