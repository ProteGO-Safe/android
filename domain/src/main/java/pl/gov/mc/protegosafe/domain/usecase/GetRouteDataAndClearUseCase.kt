package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.RouteRepository

class GetRouteDataAndClearUseCase(
    private val routeRepository: RouteRepository
) {

    fun execute(): Single<String> {
        return Single.fromCallable {
            val data = routeRepository.getLatestRoute()
            routeRepository.saveRoute("")
            data
        }
    }
}
