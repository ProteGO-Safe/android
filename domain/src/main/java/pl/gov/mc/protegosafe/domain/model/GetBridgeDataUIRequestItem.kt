package pl.gov.mc.protegosafe.domain.model

data class GetBridgeDataUIRequestItem(
    override val bridgeDataType: OutgoingBridgeDataType,
    val payload: String,
    val requestId: String
) : UIRequest
