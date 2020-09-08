package pl.gov.mc.protegosafe.domain.model

enum class OutgoingBridgeDataType(val code: Int) {
    NOTIFICATION_DATA(2),
    APP_LIFECYCLE_STATE(11),
    TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS(43),
    SERVICES_STATUS(51),
    ANALYZE_RESULT(61),
    APP_VERSION(62),
    SYSTEM_LANGUAGE(63);

    companion object {
        fun valueOf(value: Int): OutgoingBridgeDataType =
            values().find { it.code == value } ?: throw IllegalAccessException()
    }
}
