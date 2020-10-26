package pl.gov.mc.protegosafe.domain.model

data class SetBridgeDataUIRequestItem(
    override val bridgeDataType: IncomingBridgeDataType,
) : UIRequest
