package pl.gov.mc.protegosafe.domain.model

interface OutgoingBridgePayloadMapper {
    fun toDistrictActionItem(payload: String): DistrictActionItem
    fun toPinItem(payload: String): PinItem
    fun areCovidStatsNotifcationsAllowed(payload: String): Boolean
}
