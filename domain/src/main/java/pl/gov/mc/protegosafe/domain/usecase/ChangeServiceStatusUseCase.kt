package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem.DISABLE_EXPOSURE_NOTIFICATION_SERVICE
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem.ENABLE_BLUETOOTH
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem.ENABLE_EXPOSURE_NOTIFICATION_SERVICE
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem.ENABLE_LOCATION
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem.ENABLE_NOTIFICATION
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem.UNKNOWN
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper

class ChangeServiceStatusUseCase(
    private val startExposureNotificationUseCase: StartExposureNotificationUseCase,
    private val stopExposureNotificationUseCase: StopExposureNotificationUseCase,
    private val incomingBridgePayloadMapper: IncomingBridgePayloadMapper
) {

    private lateinit var onResultActionRequired: (ActionRequiredItem) -> Unit

    fun execute(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable =
        Single.fromCallable { incomingBridgePayloadMapper.toChangeStatusRequestItemList(payload) }
            .doOnSuccess { this.onResultActionRequired = onResultActionRequired }
            .map {
                val requireActionsList = arrayListOf<Completable>()
                it.forEach { request ->
                    requireActionsList.add(getActions(request))
            }
            return@map requireActionsList.asIterable()
            }
            .flatMapCompletable { listOfRequests ->
                return@flatMapCompletable Completable.merge(listOfRequests)
            }

    private fun getActions(changeStatusRequestItem: ChangeStatusRequestItem): Completable {
        return when (changeStatusRequestItem) {
            ENABLE_EXPOSURE_NOTIFICATION_SERVICE -> {
                startExposureNotificationUseCase.execute()
                    .andThen(performRequireAction(ActionRequiredItem.SendServicesStatus))
            }
            DISABLE_EXPOSURE_NOTIFICATION_SERVICE -> {
                stopExposureNotificationUseCase.execute()
                    .andThen(performRequireAction(ActionRequiredItem.SendServicesStatus))
            }
            ENABLE_BLUETOOTH -> {
                performRequireAction(ActionRequiredItem.RequestEnableBluetooth)
            }
            ENABLE_LOCATION -> {
                performRequireAction(ActionRequiredItem.RequestEnableLocation)
            }
            ENABLE_NOTIFICATION -> {
                performRequireAction(ActionRequiredItem.RequestEnableNotifications)
            }
            UNKNOWN -> {
                Completable.error { Throwable("Unknown change state request") }
            }
        }
    }

    private fun performRequireAction(
        actionRequiredItem: ActionRequiredItem
    ): Completable {
        return Completable.fromAction {
            onResultActionRequired(actionRequiredItem)
        }
    }
}
