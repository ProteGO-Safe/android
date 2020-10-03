package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.AppRepository

class GetAppVersionNameUseCase(
    private val appRepository: AppRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return appRepository.getVersionName()
            .flatMap { getResult(it) }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(versionName: String): Single<String> {
        return Single.fromCallable { resultComposer.composeAppVersionNameResult(versionName) }
    }
}
