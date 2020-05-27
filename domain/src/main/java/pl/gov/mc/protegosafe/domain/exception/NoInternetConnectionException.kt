package pl.gov.mc.protegosafe.domain.exception

class NoInternetConnectionException(
    override val message: String = "No Internet connection available."
) : Exception()
