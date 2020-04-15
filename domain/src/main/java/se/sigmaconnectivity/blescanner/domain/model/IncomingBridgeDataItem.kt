package se.sigmaconnectivity.blescanner.domain.model

data class IncomingBridgeDataItem (
    val type: IncomingBridgeDataType,
    val payload: String
)