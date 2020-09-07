package pl.gov.mc.protegosafe.domain.model

sealed class ActionRequiredItem {
    object RequestEnableBluetooth : ActionRequiredItem()
    object RequestEnableLocation : ActionRequiredItem()
    object RequestEnableNotifications : ActionRequiredItem()
    object SendServicesStatus : ActionRequiredItem()
    object ExposureNotificationPermissionGranted : ActionRequiredItem()
    object TemporaryExposureKeysPermissionGranted : ActionRequiredItem()
    object SendTemporaryExposureKeysUploadFailure : ActionRequiredItem()
    object ClearExposureNotificationData : ActionRequiredItem()
    object RestartActivity : ActionRequiredItem()
    data class SendTemporaryExposureKeysUploadResult(val dataJson: String) : ActionRequiredItem() {
        val dataType = OutgoingBridgeDataType.TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS.code
    }
}
