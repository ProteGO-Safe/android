package pl.gov.mc.protegosafe.domain.model

sealed class ConnectionException(
    override val message: String = "Connection error occurred"
) : Exception() {
    object NotFound : ConnectionException()
    object Other : ConnectionException()
}
