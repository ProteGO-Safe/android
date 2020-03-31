package pl.gov.mc.protego.bluetooth.advertiser

import java.util.*

interface AdvertiserInterface {
    /**
     * Update advertised token data
     *
     * @param data Token data available during advertisement.
     * @param expirationDate Date after which token is expired.
     */
    fun updateTokenData(data: ByteArray, expirationDate: Date)
}