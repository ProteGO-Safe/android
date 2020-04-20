package pl.gov.mc.protegosafe.domain.model

enum class OutgoingBridgeDataType(val code: Int) {
    NOTIFICATION_DATA(2);
    companion object {
        fun valueOf(value: Int): OutgoingBridgeDataType = values().find { it.code == value } ?: throw IllegalAccessException()
    }
}