package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
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
