package pl.gov.mc.protegosafe.domain.model

enum class ActivityRequest(val requestCode: Int) {
    UNKNOWN(-1),
    ENABLE_BLUETOOTH(1),
    ENABLE_LOCATION(2),
    ENABLE_NOTIFICATIONS(3),
    CLEAR_EXPOSURE_NOTIFICATION_DATA(4),
    START_EXPOSURE_NOTIFICATION(ResolutionRequest.START_EXPOSURE_NOTIFICATION.code),
    ACCESS_TEMPORARY_EXPOSURE_KEYS(ResolutionRequest.ACCESS_TEMPORARY_EXPOSURE_KEYS.code),
    ;

    companion object {
        fun valueOf(value: Int): ActivityRequest =
            values().find { it.requestCode == value } ?: UNKNOWN
    }
}
