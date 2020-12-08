package pl.gov.mc.protegosafe.domain.repository

interface RouteRepository {
    fun getLatestRoute(): String
    fun saveRoute(route: String)
    fun uriToRoute(uriString: String): String
}
