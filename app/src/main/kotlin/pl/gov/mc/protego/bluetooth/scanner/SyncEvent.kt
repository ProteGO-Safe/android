package pl.gov.mc.protego.bluetooth.scanner

import pl.gov.mc.protego.bluetooth.beacon.BeaconIdRemote


sealed class SyncEvent {

    sealed class Connection : SyncEvent() {
        object Connecting : Connection()
        object DiscoveringServices : Connection()
        sealed class Error : Connection() {
            data class Gatt(val status: Int) : Error()
            object AdapterOff : Error()
            object Timeout : Error()
            object Duplicate : Error()
        }
    }

    sealed class Process : SyncEvent() {
        object Start : Process()

        sealed class ReadBeaconId : Process() {
            data class Valid(val beaconIdRemote: BeaconIdRemote) : ReadBeaconId()
            object Invalid : ReadBeaconId()
            data class Failure(val status: Int) : ReadBeaconId()
        }

        sealed class WrittenBeaconId : Process() {
            object Success : WrittenBeaconId()
            object Invalid : WrittenBeaconId()
            data class Failure(val status: Int) : WrittenBeaconId()
        }

        sealed class End : Process() {
            object NoProteGoAttributes : End()
            object Finished : End()
        }
    }

    fun className(): String = when (this) {
        Connection.Connecting -> "Connection.Connecting"
        Connection.DiscoveringServices -> "Connection.DiscoveringServices"
        is Connection.Error.Gatt -> "Connection.Error.Gatt(${this.status})"
        Connection.Error.AdapterOff -> "Connection.Error.AdapterOff"
        Connection.Error.Timeout -> "Connection.Error.Timeout"
        Connection.Error.Duplicate -> "Connection.Error.Duplicate"
        Process.Start -> "Process.Start"
        is Process.ReadBeaconId.Valid -> "Process.ReadBeaconId.Valid"
        Process.ReadBeaconId.Invalid -> "Process.ReadBeaconId.Invalid"
        is Process.ReadBeaconId.Failure -> "Process.ReadBeaconId.Failure(${this.status})"
        Process.WrittenBeaconId.Success -> "Process.WrittenBeaconId.Success"
        Process.WrittenBeaconId.Invalid -> "Process.WrittenBeaconId.Invalid"
        is Process.WrittenBeaconId.Failure -> "Process.WrittenBeaconId.Failure(${this.status})"
        Process.End.NoProteGoAttributes -> "Process.End.NoProteGoAttributes"
        Process.End.Finished -> "Process.End.Finished"
    }
}