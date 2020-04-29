package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

class TrackTempIdUseCase(
    private val openTraceRepository: OpenTraceRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Observable<String> = openTraceRepository.trackTempId
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
}