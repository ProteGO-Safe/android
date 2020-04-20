package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType

class OnGetBridgeDataUseCase(
    private val getNotificationDataAndClear: GetNotificationDataAndClearUseCase
) {

    fun execute(type: OutgoingBridgeDataType): String {
        when(type) {
            OutgoingBridgeDataType.NOTIFICATION_DATA -> {
                return getNotificationDataAndClear.execute()
            }
            else -> {
                throw IllegalArgumentException("OutgoingBridgeDataType has wrong value")
            }
        }
    }
}