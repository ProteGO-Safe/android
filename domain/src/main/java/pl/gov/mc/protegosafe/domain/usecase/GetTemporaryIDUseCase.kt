package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread

class GetTemporaryIDUseCase(
    private val openTraceRepository: OpenTraceRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Completable {
        return openTraceRepository.getTemporaryIDs()
            .andThen(openTraceRepository.getHandShakePin())
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}