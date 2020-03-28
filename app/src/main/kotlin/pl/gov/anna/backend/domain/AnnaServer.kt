package pl.gov.anna.backend.domain

import io.reactivex.Single
import pl.gov.anna.backend.api.RegistrationService

class AnnaServer(
    private val registrationService: RegistrationService
) {

    fun startRegistration(msisdn: String): Single<Boolean> {
        return registrationService.startRegistration(msisdn).map { true }
    }
}