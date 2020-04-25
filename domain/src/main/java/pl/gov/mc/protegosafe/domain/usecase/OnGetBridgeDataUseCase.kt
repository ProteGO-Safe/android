package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType

class OnGetBridgeDataUseCase(
    private val getNotificationDataAndClear: GetNotificationDataAndClearUseCase,
    private val getServicesStatusUseCase: GetServicesStatusUseCase
) {

    fun execute(type: OutgoingBridgeDataType): String {
        return when(type) {
            OutgoingBridgeDataType.NOTIFICATION_DATA -> {
                getNotificationDataAndClear.execute()
            }
            OutgoingBridgeDataType.SERVICES_STATUS -> {
                getServicesStatusUseCase.execute()
            }
            else -> {
                throw IllegalArgumentException("OutgoingBridgeDataType has wrong value")
            }
        }
    }
}