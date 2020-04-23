package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.PushNotifier
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.TriageRepository
import pl.gov.mc.protegosafe.domain.model.PushNotificationData
import pl.gov.mc.protegosafe.domain.model.PushNotificationTopic
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import java.util.*

class TrackServiceEnabledUseCase(
    private val deviceRepository: DeviceRepository,
    private val postExecutionThread: PostExecutionThread
    ) {

    fun execute(): Observable<Boolean> = deviceRepository.traceServiceEnabled
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
}