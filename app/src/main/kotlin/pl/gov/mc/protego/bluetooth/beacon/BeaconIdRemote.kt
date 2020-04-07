package pl.gov.mc.protego.bluetooth.beacon


data class BeaconIdRemote(val id: BeaconId, val rssi: Int?, val source: BeaconIdSource)