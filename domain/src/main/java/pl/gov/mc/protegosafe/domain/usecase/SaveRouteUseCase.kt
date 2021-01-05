package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.RouteRepository

class SaveRouteUseCase(
    private val routeRepository: RouteRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(data: String): Completable {
        return Completable.fromAction {
            routeRepository.saveRoute(data)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
