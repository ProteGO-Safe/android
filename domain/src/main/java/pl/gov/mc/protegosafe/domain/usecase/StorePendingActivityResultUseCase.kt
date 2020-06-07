package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActivityResult
import pl.gov.mc.protegosafe.domain.repository.PendingActivityResultRepository

class StorePendingActivityResultUseCase(
    private val pendingActivityResultRepository: PendingActivityResultRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(activityResult: ActivityResult): Completable =
        pendingActivityResultRepository.setActivityResult(activityResult)
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
}
