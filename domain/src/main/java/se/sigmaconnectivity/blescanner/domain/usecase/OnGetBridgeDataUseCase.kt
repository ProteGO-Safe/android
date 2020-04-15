package se.sigmaconnectivity.blescanner.domain.usecase

import se.sigmaconnectivity.blescanner.domain.model.OutgoingBridgeDataType

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