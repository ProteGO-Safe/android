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

        fun execute(isTrackingEnabled: Boolean): Completable = if (isTrackingEnabled) {
            singInAndStartBLEMonitoringServiceUseCase.execute()
        } else {
            Completable.fromAction { stopBLEMonitoringServiceUseCase.execute() }
        }
            .andThen ( trackingRepository.saveTrackingAgreement(isTrackingEnabled) )
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
}
