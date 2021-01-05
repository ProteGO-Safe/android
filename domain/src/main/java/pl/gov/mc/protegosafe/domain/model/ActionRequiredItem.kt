package pl.gov.mc.protegosafe.domain.model

sealed class ActionRequiredItem {
    object RequestEnableBluetooth : ActionRequiredItem()
    object RequestEnableLocation : ActionRequiredItem()
    object RequestEnableNotifications : ActionRequiredItem()
    object SendServicesStatus : ActionRequiredItem()
    object ExposureNotificationPermissionGranted : ActionRequiredItem()
    object TemporaryExposureKeysPermissionGranted : ActionRequiredItem()
    object TemporaryExposureKeysPermissionDenied : ActionRequiredItem()
    object ClearData : ActionRequiredItem()
    object RestartActivity : ActionRequiredItem()
    object CloseApp : ActionRequiredItem()
    object AppReview : ActionRequiredItem()
    data class SendTemporaryExposureKeysUploadResult(val dataJson: String) : ActionRequiredItem() {
        val dataType = OutgoingBridgeDataType.TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS.code
    }
    object UpdateTestSubscription : ActionRequiredItem()
    object UpdateCovidStats : ActionRequiredItem()
}
