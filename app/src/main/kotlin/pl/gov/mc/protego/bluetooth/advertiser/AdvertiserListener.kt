package pl.gov.mc.protego.bluetooth.advertiser

import java.util.*

interface AdvertiserListener {
    /**
     * Callback invoked when token data was successfully retrieved from the surrounding peripheral.
     *
     * Note: Make sure this function call is **not blocking**.
     *
     * @param advertiser Advertiser calling this function.
     * @param data Token data synchronized with other device.
     * @param rssi RSSI value when data was synchronized.
     */
    fun synchronizedTokenData(advertiser: AdvertiserInterface, data: ByteArray, rssi: Int?)

    /**
     * This function is a hint that data we want to send to other device is expired and need
     * replacement. User may call `updateTokenData` on advertiser to update token data.
     * If token data is not updated during this call, incoming request will be rejected.
     *
     * Note: Make sure this function call is **not blocking**.
     *
     * @param advertiser Advertiser Advertiser calling this function.
     * @param previousTokenData Previous, expired token data if available
     */
    fun tokenDataExpired(advertiser: AdvertiserInterface, previousTokenData: Pair<ByteArray, Date>?)
}