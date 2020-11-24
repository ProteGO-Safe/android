package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.exception.NoInternetConnectionException
import pl.gov.mc.protegosafe.domain.exception.UploadException
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getDayStartRollingNumber
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.ConnectionException
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.model.InteroperabilityItem
import pl.gov.mc.protegosafe.domain.model.RetrofitExceptionMapper
import pl.gov.mc.protegosafe.domain.model.SetBridgeDataUIRequestItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem.Companion.ROLLING_PERIOD_MAX
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadRequestItem
import pl.gov.mc.protegosafe.domain.repository.CacheStore
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.repository.KeyUploadSystemInfoRepository
import pl.gov.mc.protegosafe.domain.repository.TemporaryExposureKeysUploadRepository
import java.lang.Exception

class UploadTemporaryExposureKeysUseCase(
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val keyUploadSystemInfoRepository: KeyUploadSystemInfoRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val payloadMapper: IncomingBridgePayloadMapper,
    private val temporaryExposureKeysUploadRepository: TemporaryExposureKeysUploadRepository,
    private val internetConnectionManager: InternetConnectionManager,
    private val retrofitExceptionMapper: RetrofitExceptionMapper,
    private val cacheStore: CacheStore,
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
        return getTemporaryExposureKeys(payload)
            .map {
                validateTemporaryExposureKeysCorrectness(it)
            }
            .onErrorResumeNext {
                temporaryExposureKeysUploadRepository.cacheRequestPayload(payload)
                    .andThen(cacheUIRequest())
                    .andThen(Single.error(it))
            }
            .flatMapCompletable {
                getAccessTokenAndUploadKeys(payload, onResultActionRequired, it)
            }
            .andThen(sendSuccessResult(onResultActionRequired))
    }

    private fun validateTemporaryExposureKeysCorrectness(
        keys: List<TemporaryExposureKeyItem>
    ): List<TemporaryExposureKeyItem> {
        return when {
            keys.isEmpty() -> {
                throw UploadException.NoKeysError
            }
            keys.size > UploadException.TotalLimitExceededError.LIMIT -> {
                throw UploadException.TotalLimitExceededError
            }
            !areTemporaryExposureKeysPerDayCorrect(keys) -> {
                throw UploadException.DailyLimitExceededError
            }
            else -> {
                keys
            }
        }
    }

    private fun areTemporaryExposureKeysPerDayCorrect(
        keys: List<TemporaryExposureKeyItem>
    ): Boolean {
        return keys
            .filter { it.rollingPeriod != ROLLING_PERIOD_MAX }
            .ifEmpty { return true }
            .sortedBy { it.rollingPeriod }
            .groupBy { it.getDayStartRollingNumber() }
            .map { it.value.size }
            .maxOrNull()
            ?.let { it <= UploadException.DailyLimitExceededError.LIMIT }
            ?: true
    }

    private fun cachePayloadAndReturnConnectionError(payload: String): Completable {
        return temporaryExposureKeysUploadRepository.cacheRequestPayload(payload)
            .andThen(
                cacheUIRequest()
            )
            .andThen(
                Completable.error(NoInternetConnectionException())
            )
    }

    private fun getAccessToken(pin: PinItem): Single<String> =
        temporaryExposureKeysUploadRepository.getCachedRequestAccessToken()
            .onErrorResumeNext {
                temporaryExposureKeysUploadRepository.getAccessToken(pin)
                    .onErrorResumeNext {
                        Single.error {
                            retrofitExceptionMapper.toConnectionError(it as Exception).let {
                                return@error when (it) {
                                    ConnectionException.NotFound -> {
                                        UploadException.PinVerificationFailed
                                    }
                                    else -> {
                                        UploadException.PinVerificationError
                                    }
                                }
                            }
                        }
                    }
            }

    private fun getAccessTokenAndUploadKeys(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit,
        keys: List<TemporaryExposureKeyItem>
    ): Completable = getAccessToken(payloadMapper.toPinItem(payload))
        .flatMapCompletable { uploadKeys(it, payloadMapper.toInteroperabilityItem(payload), keys) }
        .onErrorResumeNext {
            when (it) {
                UploadException.PinVerificationFailed -> {
                    sendFailureResultAndThrowError(it, onResultActionRequired)
                }
                else -> {
                    temporaryExposureKeysUploadRepository.cacheRequestPayload(payload)
                        .andThen(Completable.error(it))
                }
            }
        }

    private fun uploadKeys(
        accessToken: String,
        interoperabilityItem: InteroperabilityItem,
        keys: List<TemporaryExposureKeyItem>
    ): Completable =
        getUploadRequestData(accessToken, interoperabilityItem, keys)
            .flatMapCompletable { uploadTemporaryExposureKeys(it) }
            .onErrorResumeNext {
                temporaryExposureKeysUploadRepository.cacheRequestAccessToken(accessToken)
                    .andThen(Completable.error(UploadException.UploadTemporaryExposureKeysError))
            }

    private fun sendSuccessResult(onResultActionRequired: (ActionRequiredItem) -> Unit) =
        sendResult(TemporaryExposureKeysUploadState.SUCCESS, onResultActionRequired)

    private fun sendFailureResultAndThrowError(
        error: Throwable,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        val state = if (error is NoInternetConnectionException) {
            TemporaryExposureKeysUploadState.CANCELED
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
        interoperabilityItem: InteroperabilityItem,
        keys: List<TemporaryExposureKeyItem>
    ): Single<TemporaryExposureKeysUploadRequestItem> =
        Single.just(
            TemporaryExposureKeysUploadRequestItem(
                keys,
                interoperabilityItem.isInteroperabilityEnabled,
                keyUploadSystemInfoRepository.platform,
                keyUploadSystemInfoRepository.appPackageName,
                keyUploadSystemInfoRepository.regions,
                accessToken
            )
        )

    private fun getTemporaryExposureKeys(
        payload: String
    ): Single<List<TemporaryExposureKeyItem>> =
        exposureNotificationRepository.getTemporaryExposureKeyHistory()
            .onErrorResumeNext {
                when (it) {
                    is ExposureNotificationActionNotResolvedException -> {
                        temporaryExposureKeysUploadRepository.cacheRequestPayload(payload)
                            .andThen(Single.error(it))
                    }
                    else -> {
                        temporaryExposureKeysUploadRepository.cacheRequestPayload(payload)
                            .andThen(Single.error(UploadException.GetTemporaryExposureKeysError))
                    }
                }
            }

    private fun cacheUIRequest(): Completable {
        return cacheStore.cacheUiRequest(
            SetBridgeDataUIRequestItem(
                IncomingBridgeDataType.REQUEST_TEMPORARY_EXPOSURE_KEYS_UPLOAD
            )
        )
    }
}
