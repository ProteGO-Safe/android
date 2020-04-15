package se.sigmaconnectivity.blescanner.domain.usecase

import io.reactivex.Completable
import se.sigmaconnectivity.blescanner.domain.executor.PostExecutionThread
import se.sigmaconnectivity.blescanner.domain.model.IncomingBridgeDataItem

class OnSetBridgeDataUseCase(
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(input: IncomingBridgeDataItem): Completable = Completable.fromAction {
        //TODO: logic here
    }
}