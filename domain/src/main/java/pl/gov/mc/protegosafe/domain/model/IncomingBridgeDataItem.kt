package pl.gov.mc.protegosafe.domain.model

data class IncomingBridgeDataItem(
    val type: IncomingBridgeDataType,
    val payload: String
)
