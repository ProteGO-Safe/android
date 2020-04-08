package pl.gov.mc.protego.bluetooth.advertiser

interface ProteGOGattServerCallback {
    fun gattServerStarted(gattServer: ProteGOGattServer)
    fun gattServerFailed(gattServer: ProteGOGattServer, status: Int)
}