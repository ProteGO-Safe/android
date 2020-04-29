package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.ClearItem
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

class ClearBtDataUseCase(
    private val openTraceRepository: OpenTraceRepository,
    private val enableBTServiceUseCase: EnableBTServiceUseCase
) {
    fun execute(clearItem: ClearItem): Completable =
        enableBTServiceUseCase.execute(false)
            .andThen(Completable.fromAction {
                if (clearItem.clearBtData) {
                    openTraceRepository.clearTracingData()
                }
            })
}