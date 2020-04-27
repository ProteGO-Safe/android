package pl.gov.mc.protegosafe.domain

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

class DumpTraceDataUseCase(
    private val openTraceRepository: OpenTraceRepository,
    private val postExecutionThread: PostExecutionThread

) {
    fun execute(uploadToken: String) = openTraceRepository.dumpTraceData(uploadToken)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
}