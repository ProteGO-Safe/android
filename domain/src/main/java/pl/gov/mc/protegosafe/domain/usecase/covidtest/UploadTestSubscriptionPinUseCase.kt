package pl.gov.mc.protegosafe.domain.usecase.covidtest

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.exception.CovidTestNotCompatibleDeviceException
import pl.gov.mc.protegosafe.domain.exception.NoInternetConnectionException
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.ResultStatus
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository
import java.net.UnknownHostException
import java.util.UUID

class UploadTestSubscriptionPinUseCase(
    private val covidTestRepository: CovidTestRepository,
    private val payloadMapper: OutgoingBridgePayloadMapper,
    private val internetConnectionManager: InternetConnectionManager,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(
        payload: String,
    ): Single<String> {
        return checkIfDeviceCompatibleOrError()
            .andThen(checkInternet())
            .flatMap {
                if (it.isConnected()) {
                    parsePayload(payload)
                        .flatMap { pinItem ->
                            startUpload(pinItem.pin)
                        }
                } else {
                    throw NoInternetConnectionException()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun checkIfDeviceCompatibleOrError(): Completable {
        return covidTestRepository.isDeviceCompatible()
            .flatMapCompletable { isAvailable ->
                if (isAvailable) {
                    Completable.complete()
                } else {
                    Completable.fromAction {
                        throw CovidTestNotCompatibleDeviceException()
                    }
                }
            }
    }

    private fun startUpload(pin: String): Single<String> {
        return covidTestRepository.getTestSubscription(pin, UUID.randomUUID().toString())
            .flatMapCompletable { testSubscription ->
                saveData(pin, testSubscription)
            }
            .andThen(
                getResult(ResultStatus.SUCCESS)
            )
            .onErrorResumeNext {
                if (it is UnknownHostException) {
                    throw it
                } else {
                    getResult(ResultStatus.FAILURE)
                }
            }
    }

    private fun saveData(pin: String, testSubscription: TestSubscriptionItem): Completable {
        return covidTestRepository.saveTestSubscription(testSubscription)
            .andThen(covidTestRepository.saveTestSubscriptionPin(pin))
    }

    private fun checkInternet(): Single<InternetConnectionManager.InternetConnectionStatus> {
        return Single.fromCallable {
            internetConnectionManager.getInternetConnectionStatus()
        }
    }

    private fun parsePayload(payload: String): Single<PinItem> {
        return Single.fromCallable {
            payloadMapper.toPinItem(payload)
        }
    }

    private fun getResult(resultStatus: ResultStatus): Single<String> {
        return Single.fromCallable {
            resultComposer.composeUploadTestPinResult(resultStatus)
        }
    }
}
