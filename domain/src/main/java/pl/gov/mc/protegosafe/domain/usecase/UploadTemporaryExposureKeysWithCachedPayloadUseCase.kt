package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.repository.TemporaryExposureKeysUploadRepository

class UploadTemporaryExposureKeysWithCachedPayloadUseCase(
    private val uploadTemporaryExposureKeysUseCase: UploadTemporaryExposureKeysUseCase,
    private val temporaryExposureKeysUploadRepository: TemporaryExposureKeysUploadRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(onResultActionRequired: (ActionRequiredItem) -> Unit): Completable =
        temporaryExposureKeysUploadRepository.getCachedRequestPayload()
            .flatMapCompletable {
                uploadTemporaryExposureKeysUseCase.execute(it, onResultActionRequired)
            }
            .andThen(temporaryExposureKeysUploadRepository.clearCache())
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
}
