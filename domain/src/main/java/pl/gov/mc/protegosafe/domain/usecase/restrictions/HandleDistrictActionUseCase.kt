package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.DistrictActionItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class HandleDistrictActionUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val outgoingBridgePayloadMapper: OutgoingBridgePayloadMapper,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(payload: String): Completable {
        return parsePayload(payload)
            .flatMapCompletable {
                if (it.type == DistrictActionItem.ActionType.ADD) {
                    covidInfoRepository.addDistrictToSubscribed(it.districtId)
                } else {
                    covidInfoRepository.deleteDistrictFromSubscribed(it.districtId)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun parsePayload(payload: String): Single<DistrictActionItem> {
        return Single.fromCallable {
            outgoingBridgePayloadMapper.toDistrictActionItem(payload)
        }
    }
}
