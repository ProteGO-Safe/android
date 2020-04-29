package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.repository.ToastRepository
import pl.gov.mc.protegosafe.domain.repository.TrackingRepository

class EnableBTServiceUseCase(
    private val trackingRepository: TrackingRepository,
    private val singInAndStartBLEMonitoringServiceUseCase:
    SignInAndStartBLEMonitoringServiceUseCase,
    private val stopBLEMonitoringServiceUseCase: StopBLEMonitoringServiceUseCase,
    private val toastRepository: ToastRepository
) {

        fun execute(isTrackingEnabled: Boolean): Completable = if (isTrackingEnabled) {
            singInAndStartBLEMonitoringServiceUseCase.execute()
        } else {
            Completable.fromAction { stopBLEMonitoringServiceUseCase.execute() }
        }
            .andThen ( trackingRepository.saveTrackingAgreement(isTrackingEnabled) )
            .also {
                toastRepository.showIsBtServiceEnabledInfo(isTrackingEnabled)
            }
}
