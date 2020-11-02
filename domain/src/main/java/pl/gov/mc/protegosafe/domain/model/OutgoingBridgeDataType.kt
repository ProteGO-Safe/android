package pl.gov.mc.protegosafe.domain.model

enum class OutgoingBridgeDataType(override val code: Int) : UIRequestBridgeDataType {
    NOTIFICATION_DATA(2),
    APP_LIFECYCLE_STATE(11),
    TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS(43),
    SERVICES_STATUS(51),
    ANALYZE_RESULT(61),
    APP_VERSION(62),
    SYSTEM_LANGUAGE(63),
    GET_FONT_SCALE(65),
    EXPOSURE_RISK_CANCELLATION(66),
    DISTRICTS_STATUS(70),
    UPDATE_DISTRICTS_STATUSES(71),
    DISTRICT_ACTION(72),
    GET_SUBSCRIBED_DISTRICTS(73),
    UPLOAD_COVID_TEST_PIN(80),
    GET_COVID_TEST_SUBSCRIPTION_STATUS(81),
    GET_COVID_TEST_SUBSCRIPTION_PIN(82);

    companion object {
        fun valueOf(value: Int): OutgoingBridgeDataType =
            values().find { it.code == value } ?: throw IllegalAccessException()
    }
}
