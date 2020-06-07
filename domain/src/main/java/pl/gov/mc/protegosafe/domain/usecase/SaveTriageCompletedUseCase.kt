package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.TriageRepository

class SaveTriageCompletedUseCase(
    private val triageRepository: TriageRepository
) {
    fun execute(payload: String) =
        Single.fromCallable { triageRepository.parseBridgePayload(payload) }
            .flatMapCompletable {
                Completable.fromAction {
                    triageRepository.saveTriageCompletedTimestamp(it.timestamp)
                }
            }
}
