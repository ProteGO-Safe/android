package pl.gov.mc.protegosafe.domain.exception

class CovidTestNotCompatibleDeviceException(
    override val message: String = "Covid test is not available, phone not secured enough."
) : Exception()
