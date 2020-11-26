package pl.gov.mc.protegosafe.domain.model

enum class IncomingBridgeDataType(override val code: Int) : UIRequestBridgeDataType {
    UNKNOWN(-1),
    TRIAGE(1),
    REQUEST_CLEAR_DATA(37),
    REQUEST_TEMPORARY_EXPOSURE_KEYS_UPLOAD(43),
    SERVICES_STATUS(51),
    REQUEST_SERVICE_STATUS_CHANGE(52),
    SYSTEM_LANGUAGE(63),
    CLOSE_APPLICATION(64),
    APP_REVIEW(68);

    companion object {
        fun valueOf(value: Int): IncomingBridgeDataType =
            values().find { it.code == value } ?: UNKNOWN
    }
}
