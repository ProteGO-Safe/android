package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.PinMapper
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.model.exposeNotification.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.model.exposeNotification.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.model.exposeNotification.TemporaryExposureKeysUploadRequestData
import pl.gov.mc.protegosafe.domain.model.exposeNotification.toDiagnosisKeyList
import pl.gov.mc.protegosafe.domain.repository.CloudRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.repository.KeyUploadSystemInfoRepository
import pl.gov.mc.protegosafe.domain.repository.TemporaryExposureKeysUploadRepository

class UploadTemporaryExposureKeysUseCase(
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val keyUploadSystemInfoRepository: KeyUploadSystemInfoRepository,
    private val cloudRepository: CloudRepository,
    private val safetyNetAttestationWrapper: SafetyNetAttestationWrapper,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val pinMapper: PinMapper,
    private val temporaryExposureKeysUploadRepository: TemporaryExposureKeysUploadRepository,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable =
        getTemporaryExposureKeys(payload, onResultActionRequired)
            .flatMapCompletable { getAccessTokenAndUploadKeys(payload, onResultActionRequired, it) }
            .andThen(sendSuccessResult(onResultActionRequired))
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)

    private fun getAccessToken(pin: PinItem): Single<String> =
        cloudRepository.getAccessToken(pin)

    private fun getAccessTokenAndUploadKeys(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit,
        keys: List<TemporaryExposureKeyItem>
    ): Completable = getAccessToken(pinMapper.toEntity(payload))
        .flatMapCompletable { uploadKeys(it, keys) }
        .onErrorResumeNext { sendFailureResultAndThrowError(it, onResultActionRequired) }

    private fun uploadKeys(
        accessToken: String,
        keys: List<TemporaryExposureKeyItem>
    ): Completable =
        getUploadRequestData(accessToken, keys)
            .flatMapCompletable { uploadTemporaryExposureKeys(it) }

    private fun sendSuccessResult(onResultActionRequired: (ActionRequiredItem) -> Unit) =
        sendResult(TemporaryExposureKeysUploadState.SUCCESS, onResultActionRequired)

    private fun sendFailureResultAndThrowError(
        error: Throwable,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable =
        sendResult(TemporaryExposureKeysUploadState.FAILURE, onResultActionRequired).andThen(
            Completable.error(error)
        )

    private fun sendResult(
        state: TemporaryExposureKeysUploadState,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable =
        Completable.fromAction {
            onResultActionRequired(
                ActionRequiredItem.SendTemporaryExposureKeysUploadResult(
                    resultComposer.composeTemporaryExposureKeysUploadResult(state)
                )
            )
        }

    private fun uploadTemporaryExposureKeys(requestData: TemporaryExposureKeysUploadRequestData) =
        cloudRepository.uploadTemporaryExposureKeys(requestData)

    private fun getUploadRequestData(
        accessToken: String,
        keys: List<TemporaryExposureKeyItem>
    ): Single<TemporaryExposureKeysUploadRequestData> =
        getDeviceVerificationPayload(keys).map {
            TemporaryExposureKeysUploadRequestData(
                keys,
                keyUploadSystemInfoRepository.platform,
                it,
                keyUploadSystemInfoRepository.appPackageName,
                keyUploadSystemInfoRepository.regions,
                accessToken
            )
        }

    private fun getDeviceVerificationPayload(keys: List<TemporaryExposureKeyItem>): Single<String> =
        safetyNetAttestationWrapper.attestFor(
            keys.toDiagnosisKeyList(),
            keyUploadSystemInfoRepository.regions
        )

    private fun getTemporaryExposureKeys(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Single<List<TemporaryExposureKeyItem>> =
        exposureNotificationRepository.getTemporaryExposureKeyHistory()
            .onErrorResumeNext {
                when (it) {
                    is ExposureNotificationActionNotResolvedException -> {
                        temporaryExposureKeysUploadRepository.cacheRequestPayload(payload)
                            .andThen(Single.error(it))
                    }
                    else -> {
                        sendFailureResultAndThrowError(it, onResultActionRequired)
                            .toSingleDefault(emptyList())
                    }
                }
            }
}
