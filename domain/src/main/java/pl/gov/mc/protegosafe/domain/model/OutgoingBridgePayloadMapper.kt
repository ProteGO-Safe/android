package pl.gov.mc.protegosafe.domain.model

interface OutgoingBridgePayloadMapper {
    fun toDistrictActionItem(payload: String): DistrictActionItem
}
