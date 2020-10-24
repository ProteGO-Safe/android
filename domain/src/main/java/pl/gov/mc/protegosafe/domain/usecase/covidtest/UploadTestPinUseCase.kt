package pl.gov.mc.protegosafe.domain.usecase.covidtest

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.exception.NoInternetConnectionException
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.ResultStatus
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository
import java.net.UnknownHostException

class UploadTestPinUseCase(
    private val covidTestRepository: CovidTestRepository,
    private val payloadMapper: OutgoingBridgePayloadMapper,
    private val internetConnectionManager: InternetConnectionManager,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(payload: String): Single<String> {
        return checkInternet()
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

    private fun startUpload(pin: String): Single<String> {
        return covidTestRepository.getTestSubscriptionAccessToken(pin)
            .flatMapCompletable {
                covidTestRepository.saveTestSubscriptionAccessToken(it)
                    .andThen(covidTestRepository.saveTestSubscriptionPin(pin))
            }
            .andThen(
                Single.fromCallable {
                    resultComposer.composeUploadTestPinResult(ResultStatus.SUCCESS)
                }
            )
            .onErrorResumeNext {
                Single.fromCallable {
                    if (it is UnknownHostException) {
                        throw it
                    } else {
                        resultComposer.composeUploadTestPinResult(ResultStatus.FAILURE)
                    }
                }
            }
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
}
