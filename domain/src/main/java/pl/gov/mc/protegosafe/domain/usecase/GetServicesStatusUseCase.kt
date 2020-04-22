package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.model.ServicesStatusItem

class GetServicesStatusUseCase(
) {

    fun execute(): String {
        return """{
		"isBtSupported” = true/false,
		"isLocationEnabled” = true/false,  //only Android
		"isBtOn” = true/false,
		"isBatteryOptimizationOn” = true/false, //only Android
		"isBtServiceOn” = true/false //OpenTrace status
        """.trimIndent()
    }
}