package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import pl.gov.mc.protegosafe.domain.repository.ProtobufRepository
import java.io.File

class SaveKeysCountToAnalyzeUseCase(
    private val protobufRepository: ProtobufRepository,
    private val activitiesRepository: ActivitiesRepository,
    private val covidInfoRepository: CovidInfoRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(token: String, filesList: List<File>): Completable {
        return protobufRepository.getTemporaryExposureKeysCount(filesList)
            .flatMapCompletable {
                activitiesRepository.saveKeysCountToAnalyze(token, it)
                    .andThen(
                        covidInfoRepository.updateTotalKeysCount(it)
                    )
            }.subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
