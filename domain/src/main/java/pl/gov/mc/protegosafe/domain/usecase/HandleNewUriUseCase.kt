package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.RouteRepository

class HandleNewUriUseCase(
    private val routeRepository: RouteRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(uriString: String): Completable {
        return Single.fromCallable {
            routeRepository.uriToRoute(uriString)
        }.flatMapCompletable {
            Completable.fromAction {
                routeRepository.saveRoute(it)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
