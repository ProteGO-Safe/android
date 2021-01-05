package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository

class SaveRiskCheckActivityUseCase(
    private val activitiesRepository: ActivitiesRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(token: String, exposuresCount: Int): Completable {
        return activitiesRepository.getKeysCountForToken(token)
            .flatMapCompletable {
                activitiesRepository.saveRiskCheckActivity(it, exposuresCount)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
