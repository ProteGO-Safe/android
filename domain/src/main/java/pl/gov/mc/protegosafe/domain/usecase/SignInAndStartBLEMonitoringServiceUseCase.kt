package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.usecase.auth.SignInUseCase

class SignInAndStartBLEMonitoringServiceUseCase(
    private val startBLEMonitoringServiceUseCase: StartBLEMonitoringServiceUseCase,
    private val signInUseCase: SignInUseCase,
    private val postExecutionThread: PostExecutionThread

    ) {

    fun execute(delayMs: Long = 0): Completable = signInUseCase.execute()
            .andThen(Completable.fromAction {startBLEMonitoringServiceUseCase.execute(START_BLE_MONITOR_SERVICE_DELAY)})
}

private const val START_BLE_MONITOR_SERVICE_DELAY = 500L
