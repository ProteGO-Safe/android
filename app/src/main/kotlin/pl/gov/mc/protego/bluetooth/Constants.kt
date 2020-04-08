package pl.gov.mc.protego.bluetooth

const val AppleManufacturerId = 0x004C
const val ProteGOManufacturerId = 0x08AF
const val ProteGOManufacturerDataVersion = 0x00
// 16-bit Universally Unique Identifier (UUID) for use with ProteGO assigned to Polidea by Bluetooth SIG
// More info: https://www.bluetooth.com/specifications/assigned-numbers/16-bit-uuids-for-members/
// ProteGO use only
const val ProteGOServiceUUIDString = "0000FD6E-0000-1000-8000-00805F9B34FB"
const val ProteGOCharacteristicUUIDString = "89a60001-4f57-4c1b-9042-7ed87d723b4e"

const val PeripheralIgnoredTimeoutInSec = 60L
const val PeripheralSynchronizationTimeoutInSec = 20L
const val PeripheralIgnoredGracePeriodIfNoProteGOCharacteristicInMin = 15L
