package pl.gov.mc.protego.bluetooth

import java.util.*

const val bluetoothBaseUUIDLSB: Long = -9223371485494954757 // 0x800000805F9B34FB
const val bluetoothBaseUUIDMSB: Long = 4096 // 0x0000000000001000
const val bluetoothBaseUUIDLSBMask: Long = -1 // 0xFFFFFFFFFFFFFFFF
const val bluetoothBaseUUIDMSBMask: Long = 4294967295 // 0x00000000FFFFFFFF

fun UUID.advertisementByteLength(): Int {
    if ((mostSignificantBits and bluetoothBaseUUIDMSBMask) == bluetoothBaseUUIDMSB &&
        (leastSignificantBits and bluetoothBaseUUIDLSBMask) == bluetoothBaseUUIDLSB) {
        if (mostSignificantBits.shr(16) > 0xFFFF) {
            return 4
        }
        return 2
    }
    return 16
}