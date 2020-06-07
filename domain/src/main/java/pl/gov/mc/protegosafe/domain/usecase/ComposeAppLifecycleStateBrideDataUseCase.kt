package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer

class ComposeAppLifecycleStateBrideDataUseCase(private val resultComposer: OutgoingBridgeDataResultComposer) {
    fun execute(state: AppLifecycleState): Single<String> = Single.fromCallable {
        resultComposer.composeAppLifecycleStateResult(state)
    }
}
