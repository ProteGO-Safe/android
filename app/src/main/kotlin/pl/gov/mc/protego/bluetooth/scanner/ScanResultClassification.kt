package pl.gov.mc.protego.bluetooth.scanner

import android.os.ParcelUuid
import com.polidea.rxandroidble2.scan.ScanRecord
import com.polidea.rxandroidble2.scan.ScanResult
import pl.gov.mc.protego.bluetooth.AppleManufacturerId
import pl.gov.mc.protego.bluetooth.ProteGOManufacturerId
import pl.gov.mc.protego.bluetooth.ProteGOServiceUUIDString
import pl.gov.mc.protego.bluetooth.beacon.BeaconId


object ScanResultClassification : (ScanResult) -> ClassifiedPeripheral {

    private object ProteGOAdvertisement {
        val searchedUuid: ParcelUuid = ParcelUuid.fromString(ProteGOServiceUUIDString)
        operator fun contains(scanRecord: ScanRecord) = scanRecord.serviceUuids?.contains(searchedUuid) ?: false
    }

    private object AppleAdvertisement {
        operator fun contains(scanRecord: ScanRecord) = scanRecord.manufacturerSpecificData[AppleManufacturerId] != null
    }

    override fun invoke(scanResult: ScanResult): ClassifiedPeripheral {
        val bleDevice = scanResult.bleDevice
        return when (scanResult.scanRecord) {
            in ProteGOAdvertisement -> potentialBeaconIdOf(scanResult.scanRecord).let {
                if (it != null) ClassifiedPeripheral.ProteGO.FullAdvertisement(bleDevice, it, scanResult.rssi)
                else ClassifiedPeripheral.ProteGO.MinimalAdvertisement(bleDevice)
            }
            in AppleAdvertisement -> ClassifiedPeripheral.ProteGO.PotentialAdvertisement(bleDevice)
            else -> ClassifiedPeripheral.NonProteGO
        }
    }

    private fun potentialBeaconIdOf(scanRecord: ScanRecord): BeaconId? =
        scanRecord.manufacturerSpecificData[ProteGOManufacturerId]
            ?.let {
                val expectedManufacturerSpecificDataSize = BeaconId.byteCount + 1 /* versioning */
                if (it.size == expectedManufacturerSpecificDataSize) {
                    BeaconId(it.copyOfRange(1, expectedManufacturerSpecificDataSize), it[0].toInt())
                } else {
                    null
                }
            }

}