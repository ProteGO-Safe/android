package pl.gov.mc.protego.bluetooth.advertiser

interface ProteGoGattServerCallback {
    fun gattServerStarted(gattServer: ProteGoGattServer)
    fun gattServerFailed(gattServer: ProteGoGattServer, status: Int)
}