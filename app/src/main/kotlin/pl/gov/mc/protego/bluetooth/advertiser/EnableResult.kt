package pl.gov.mc.protego.bluetooth.advertiser

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
        data class InvalidToken(val tokenData: Pair<ByteArray, Date>?) : Failure()
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