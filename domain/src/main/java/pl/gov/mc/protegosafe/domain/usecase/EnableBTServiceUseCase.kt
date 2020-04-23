package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.TrackingRepository

class EnableBTServiceUseCase(
    private val trackingRepository: TrackingRepository,
    private val singInAndStartBLEMonitoringServiceUseCase:
    SignInAndStartBLEMonitoringServiceUseCase,
    private val stopBLEMonitoringServiceUseCase: StopBLEMonitoringServiceUseCase,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(isTrackingEnabled: Boolean): Completable {
        trackingRepository.saveTrackingAgreement(isTrackingEnabled)
        return if (isTrackingEnabled) {
            singInAndStartBLEMonitoringServiceUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(postExecutionThread.scheduler)
        } else {
            Completable.fromAction { stopBLEMonitoringServiceUseCase.execute() }
        }

    }
}