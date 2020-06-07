package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.ClearMapper
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationStatusItem
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository

class ClearExposureNotificationDataUseCase(
    private val clearMapper: ClearMapper,
    private val deviceRepository: DeviceRepository,
    private val exposureRepository: ExposureRepository
) {
    fun execute(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable =
        Single.fromCallable { clearMapper.toEntity(payload) }
            .flatMapCompletable {
                deviceRepository.getExposureNotificationStatus()
                    .flatMapCompletable {
                        if (it == ExposureNotificationStatusItem.NOT_SUPPORTED) {
                            Completable.complete()
                        } else {
                            nukeDb()
                                .andThen(getRequireAction(onResultActionRequired))
                        }
                    }
            }

    private fun nukeDb(): Completable {
        return exposureRepository.nukeDb()
            .subscribeOn(Schedulers.io())
    }

    private fun getRequireAction(
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return Completable.fromAction {
            onResultActionRequired(
                ActionRequiredItem.ClearExposureNotificationData
            )
        }
    }
}
