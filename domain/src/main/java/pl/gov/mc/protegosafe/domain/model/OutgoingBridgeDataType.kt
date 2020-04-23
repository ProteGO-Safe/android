package pl.gov.mc.protegosafe.domain.model

enum class OutgoingBridgeDataType(val code: Int) {
    NOTIFICATION_DATA(2),
    SERVICES_STATUS(31),
    PERMISSIONS_ACCEPTED(32),
    BLUETOOTH_ENABLED(33),
    BATTERY_OPTIMIZATION_SET(34),
    SERVICE_STATUS_CHANGE(36)
    ;
    companion object {
        fun valueOf(value: Int): OutgoingBridgeDataType = values().find { it.code == value } ?: throw IllegalAccessException()
    }
}