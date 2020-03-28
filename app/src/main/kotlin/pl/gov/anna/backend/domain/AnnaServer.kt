package pl.gov.anna.backend.domain

import io.reactivex.Single
import pl.gov.anna.backend.api.RegistrationResponse
import pl.gov.anna.backend.api.RegistrationService
import timber.log.Timber

class AnnaServer(
    private val registrationService: RegistrationService
) {

    fun startRegistration(msisdn: String): Single<RegistrationResponse>
            = registrationService
            .startRegistration(msisdn)
}