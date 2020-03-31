package pl.gov.mc.protego.bluetooth.advertiser

interface ProteGoGattServerCallback {
    fun gattServerStarted(gattServer: ProteGoGattServer)
    fun gattServerFailed(gattServer: ProteGoGattServer)
    fun getTokenData(gattServer: ProteGoGattServer): ByteArray?
    fun receivedTokenData(gattServer: ProteGoGattServer, byteArray: ByteArray, rssi: Int?)
}