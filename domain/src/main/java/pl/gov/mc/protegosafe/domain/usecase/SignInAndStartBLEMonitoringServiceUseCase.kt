package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository
import pl.gov.mc.protegosafe.domain.usecase.auth.SignInUseCase

class SignInAndStartBLEMonitoringServiceUseCase(
    private val startBLEMonitoringServiceUseCase: StartBLEMonitoringServiceUseCase,
    private val signInUseCase: SignInUseCase,
    private val postExecutionThread: PostExecutionThread

    ) {

    fun execute(delayMs: Long = 0) = signInUseCase.execute()
            .andThen{startBLEMonitoringServiceUseCase.execute(START_BLE_MONITOR_SERVICE_DELAY)}
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
}

private const val START_BLE_MONITOR_SERVICE_DELAY = 500L
