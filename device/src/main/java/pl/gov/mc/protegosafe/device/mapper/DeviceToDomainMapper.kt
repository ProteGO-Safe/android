package pl.gov.mc.protegosafe.device.mapper

import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem
import pl.gov.mc.protegosafe.device.model.ChangeServiceStatusRequestData

fun ChangeServiceStatusRequestData.toDomainItem(): List<ChangeStatusRequestItem> {
    val listOfChangeStatusRequests = mutableListOf<ChangeStatusRequestItem>()
    if (enableNotification != null) {
        listOfChangeStatusRequests.add(ChangeStatusRequestItem.ENABLE_NOTIFICATION)
    }
    if (enableExposureNotificationService != null) {
        listOfChangeStatusRequests.add(
            if (enableExposureNotificationService) {
                ChangeStatusRequestItem.ENABLE_EXPOSURE_NOTIFICATION_SERVICE
            } else {
                ChangeStatusRequestItem.DISABLE_EXPOSURE_NOTIFICATION_SERVICE
            }
        )
        return listOfChangeStatusRequests
    }
    if (enableBt != null) {
        listOfChangeStatusRequests.add(ChangeStatusRequestItem.ENABLE_BLUETOOTH)
    }
    if (enableLocation != null) {
        listOfChangeStatusRequests.add(ChangeStatusRequestItem.ENABLE_LOCATION)
    }
    return listOfChangeStatusRequests
}
