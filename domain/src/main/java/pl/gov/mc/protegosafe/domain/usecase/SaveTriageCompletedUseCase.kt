package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.repository.TriageRepository

class SaveTriageCompletedUseCase(
    private val triageRepository: TriageRepository,
    private val incomingBridgePayloadMapper: IncomingBridgePayloadMapper
) {
    fun execute(payload: String) =
        Single.fromCallable { incomingBridgePayloadMapper.toTriageItem(payload) }
            .flatMapCompletable {
                Completable.fromAction {
                    triageRepository.saveTriageCompletedTimestamp(it.timestamp)
                }
            }
}
