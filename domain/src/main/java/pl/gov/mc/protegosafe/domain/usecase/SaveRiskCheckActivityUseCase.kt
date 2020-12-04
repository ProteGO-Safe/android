package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import pl.gov.mc.protegosafe.domain.repository.ProtobufRepository
import java.io.File

class SaveRiskCheckActivityUseCase(
    private val protobufRepository: ProtobufRepository,
    private val activitiesRepository: ActivitiesRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(filesList: List<File>): Completable {
        return protobufRepository.getTemporaryExposureKeysAmount(filesList)
            .flatMapCompletable {
                activitiesRepository.saveRiskCheckActivity(it)
            }.subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
