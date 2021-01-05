package pl.gov.mc.protegosafe.data.repository

import android.net.Uri
import pl.gov.mc.protegosafe.data.db.RouteDataStore
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.model.RouteData
import pl.gov.mc.protegosafe.domain.repository.RouteRepository
import java.lang.IllegalStateException
import java.lang.NullPointerException

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
        if (uri.pathSegments.size > 1) {
            throw IllegalStateException("Wrong Uri pattern")
        }
        val routeName = uri.pathSegments.firstOrNull()
        val params = uri.query?.split("&")
            ?.map {
                Pair(it.substringBefore("="), it.substringAfter("="))
            }?.toMap()?.toMutableMap() ?: mutableMapOf()

        return routeName?.let {
            RouteData(routeName, params).toJson()
        } ?: throw NullPointerException("No specified route")
    }
}
