package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationStatusItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.repository.AppRepository
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository

class ClearDataUseCase(
    private val deviceRepository: DeviceRepository,
    private val appRepository: AppRepository,
    private val incomingBridgePayloadMapper: IncomingBridgePayloadMapper,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable =
        Single.fromCallable { incomingBridgePayloadMapper.toClearItem(payload) }
            .flatMapCompletable {
                when {
                    it.clearAll -> {
                        clearAllData(onResultActionRequired)
                    }
                    else -> {
                        Completable.complete()
                    }
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)

    private fun clearAllData(onResultActionRequired: (ActionRequiredItem) -> Unit): Completable {
        return clearAppData()
            .andThen(
                createDatabase()
            )
            .andThen(
                callRequiredActionIfNecessary(onResultActionRequired)
            )
    }

    private fun clearAppData(): Completable {
        return appRepository.clearAppData()
    }

    private fun createDatabase(): Completable {
        return appRepository.createRealmDatabase()
    }

    private fun callRequiredActionIfNecessary(
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return deviceRepository.getExposureNotificationStatus()
            .filter { it != ExposureNotificationStatusItem.NOT_SUPPORTED }
            .flatMapCompletable {
                Completable.fromAction {
                    onResultActionRequired(ActionRequiredItem.ClearData)
                }
            }
    }
}
