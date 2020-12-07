package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActivitiesResultItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository

class GetActivitiesResultUseCase(
    private val activitiesRepository: ActivitiesRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return activitiesRepository.getActivitiesResult()
            .flatMap {
                getResult(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(activitiesResultItem: ActivitiesResultItem): Single<String> {
        return Single.fromCallable {
            resultComposer.composeActivitiesResult(activitiesResultItem)
        }
    }
}
