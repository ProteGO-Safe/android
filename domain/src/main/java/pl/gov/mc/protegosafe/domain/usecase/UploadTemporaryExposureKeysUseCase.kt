package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.exception.NoInternetConnectionException
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.PinMapper
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadRequestItem
import pl.gov.mc.protegosafe.domain.model.toDiagnosisKeyList
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.repository.KeyUploadSystemInfoRepository
import pl.gov.mc.protegosafe.domain.repository.TemporaryExposureKeysUploadRepository

class UploadTemporaryExposureKeysUseCase(
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val keyUploadSystemInfoRepository: KeyUploadSystemInfoRepository,
    private val safetyNetAttestationWrapper: SafetyNetAttestationWrapper,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val pinMapper: PinMapper,
    private val temporaryExposureKeysUploadRepository: TemporaryExposureKeysUploadRepository,
    private val internetConnectionManager: InternetConnectionManager,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable =
        checkInternet()
            .flatMapCompletable {
                if (it.isConnected()) {
                    startUpload(payload, onResultActionRequired)
                } else {
                    cachePayloadAndReturnConnectionError(payload)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)

    private fun checkInternet(): Single<InternetConnectionManager.InternetConnectionStatus> {
        return Single.fromCallable {
            internetConnectionManager.getInternetConnectionStatus()
        }
    }

    private fun startUpload(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return getTemporaryExposureKeys(payload, onResultActionRequired)
            .flatMapCompletable {
                getAccessTokenAndUploadKeys(payload, onResultActionRequired, it)
            }
            .andThen(sendSuccessResult(onResultActionRequired))
    }

    private fun cachePayloadAndReturnConnectionError(payload: String): Completable {
        return temporaryExposureKeysUploadRepository.cacheRequestPayload(payload)
            .andThen(
                Completable.error(NoInternetConnectionException())
            )
    }

    private fun getAccessToken(pin: PinItem): Single<String> =
        temporaryExposureKeysUploadRepository.getAccessToken(pin)

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
    ): Completable {
        val state = if (error is NoInternetConnectionException) {
            TemporaryExposureKeysUploadState.OTHER
        } else {
            TemporaryExposureKeysUploadState.FAILURE
        }
        return sendResult(state, onResultActionRequired)
            .andThen(
                Completable.error(error)
            )
    }

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

    private fun uploadTemporaryExposureKeys(requestItem: TemporaryExposureKeysUploadRequestItem) =
        temporaryExposureKeysUploadRepository.uploadTemporaryExposureKeys(requestItem)

    private fun getUploadRequestData(
        accessToken: String,
        keys: List<TemporaryExposureKeyItem>
    ): Single<TemporaryExposureKeysUploadRequestItem> =
        getDeviceVerificationPayload(keys).map {
            TemporaryExposureKeysUploadRequestItem(
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
