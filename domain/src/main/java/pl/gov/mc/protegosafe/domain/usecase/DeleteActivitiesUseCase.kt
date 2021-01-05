package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository

class DeleteActivitiesUseCase(
    private val activitiesRepository: ActivitiesRepository,
    private val inputBridgePayloadMapper: IncomingBridgePayloadMapper,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(payload: String): Completable {
        return Single.fromCallable {
            inputBridgePayloadMapper.toDeleteActivities(payload)
        }.flatMapCompletable {
            activitiesRepository.deleteActivities(it)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
