package pl.gov.mc.protegosafe.data.repository

import android.net.Uri
import pl.gov.mc.protegosafe.data.db.RouteDataStore
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.model.RouteData
import pl.gov.mc.protegosafe.domain.repository.RouteRepository

class RouteRepositoryImpl(
    private val routeDataStore: RouteDataStore
) : RouteRepository {

    override fun getLatestRoute(): String {
        return routeDataStore.routeData
    }

    override fun saveRoute(route: String) {
        routeDataStore.routeData = route
    }

    override fun uriToRoute(uriString: String): String {
        val uri = Uri.parse(uriString)
        val routeName = requireNotNull(uri.pathSegments.lastOrNull()) { "No specified route" }
        val params = uri.query?.split("&")?.map {
            it.substringBefore("=") to it.substringAfter("=")
        }?.toMap()?.toMutableMap() ?: mutableMapOf()
        return RouteData(routeName, params).toJson()
    }
}
