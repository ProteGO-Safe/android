package pl.gov.mc.protegosafe.domain.model

enum class OutgoingBridgeDataType(override val code: Int) : UIRequestBridgeDataType {
    APP_LIFECYCLE_STATE(11),
    TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS(43),
    SERVICES_STATUS(51),
    ANALYZE_RESULT(61),
    APP_VERSION(62),
    SYSTEM_LANGUAGE(63),
    GET_FONT_SCALE(65),
    EXPOSURE_RISK_CANCELLATION(66),
    BACK_BUTTON_PRESSED(67),
    REROUTE_USER(69),
    DISTRICTS_STATUS(70),
    UPDATE_DISTRICTS_STATUSES(71),
    DISTRICT_ACTION(72),
    GET_SUBSCRIBED_DISTRICTS(73),
    UPLOAD_COVID_TEST_PIN(80),
    GET_COVID_TEST_SUBSCRIPTION_STATUS(81),
    GET_COVID_TEST_SUBSCRIPTION_PIN(82),
    GET_ACTIVITIES(90),
    GET_COVID_STATS_NOTIFICATION_AGREEMENT(100),
    UPDATE_COVID_STATS_NOTIFICATION_AGREEMENT(101),
    GET_COVID_STATS(102),
    GET_EN_STATS(103),
    GET_DETAILS(104);

    companion object {
        fun valueOf(value: Int): OutgoingBridgeDataType =
            values().find { it.code == value } ?: throw IllegalAccessException()
    }
}
