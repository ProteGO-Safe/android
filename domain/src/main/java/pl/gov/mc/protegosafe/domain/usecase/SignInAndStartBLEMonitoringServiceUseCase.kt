package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.usecase.auth.SignInUseCase

class SignInAndStartBLEMonitoringServiceUseCase(
    private val startBLEMonitoringServiceUseCase: StartBLEMonitoringServiceUseCase,
    private val signInUseCase: SignInUseCase
) {

    fun execute(delayMs: Long = 0): Completable = signInUseCase.execute()
            .andThen(Completable.fromAction {startBLEMonitoringServiceUseCase.execute(delayMs)})
}