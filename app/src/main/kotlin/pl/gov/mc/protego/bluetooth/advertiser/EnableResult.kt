package pl.gov.mc.protego.bluetooth.advertiser

import pl.gov.mc.protego.bluetooth.beacon.BeaconIdLocal
import java.util.*


sealed class EnableResult {
    sealed class PreconditionFailure : EnableResult() {
        object AlreadyEnabled : PreconditionFailure()
        object CannotObtainBluetoothAdapter : PreconditionFailure()
        object BluetoothOff : PreconditionFailure()
    }
    data class Started(val advertiserResult: AdvertiserResult, val serverResult: ServerResult) : EnableResult()
}

sealed class AdvertiserResult {
    object Success : AdvertiserResult()
    sealed class Failure : AdvertiserResult() {
        object NotSupported : Failure()
        object CannotObtainBluetoothAdvertiser : Failure()
        data class InvalidBeaconId(val beaconIdLocal: BeaconIdLocal?) : Failure()
    }
}

sealed class ServerResult {
    data class Success(val proteGoGattServer: ProteGoGattServer) : ServerResult()
    sealed class Failure : ServerResult() {
        object CannotObtainGattServer : Failure()
        object CannotAddService : Failure()
        object CannotAddCharacteristic : Failure()
    }
}