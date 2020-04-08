package pl.gov.mc.protego.bluetooth.scanner

import com.polidea.rxandroidble2.RxBleDevice
import pl.gov.mc.protego.bluetooth.beacon.BeaconId


sealed class ClassifiedPeripheral {
    sealed class ProteGO(val bleDevice: RxBleDevice) : ClassifiedPeripheral() {
        class FullAdvertisement(bleDevice: RxBleDevice, val beaconId: BeaconId, val rssi: Int) : ProteGO(bleDevice)

        // possibly PartialAdvertisement with partial beaconId
        class MinimalAdvertisement(bleDevice: RxBleDevice) : ProteGO(bleDevice)
        class PotentialAdvertisement(bleDevice: RxBleDevice) : ProteGO(bleDevice)
    }

    object NonProteGO : ClassifiedPeripheral()

    fun className(): String = when (this) {
        is ProteGO.FullAdvertisement -> "ProteGO.FullAdvertisement"
        is ProteGO.MinimalAdvertisement -> "ProteGO.MinimalAdvertisement"
        is ProteGO.PotentialAdvertisement -> "ProteGO.PotentialAdvertisement"
        NonProteGO -> "NonProteGO"
    }
}