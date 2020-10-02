package pl.gov.mc.protegosafe.domain.model

enum class IncomingBridgeDataType(val code: Int) {
    UNKNOWN(-1),
    TRIAGE(1),
    REQUEST_CLEAR_EXPOSURE_NOTIFICATIONS_DATA(37),
    REQUEST_TEMPORARY_EXPOSURE_KEYS_UPLOAD(43),
    SERVICES_STATUS(51),
    REQUEST_SERVICE_STATUS_CHANGE(52),
    SYSTEM_LANGUAGE(63),
    CLOSE_APPLICATION(64);

    companion object {
        fun valueOf(value: Int): IncomingBridgeDataType =
            values().find { it.code == value } ?: UNKNOWN
    }
}
