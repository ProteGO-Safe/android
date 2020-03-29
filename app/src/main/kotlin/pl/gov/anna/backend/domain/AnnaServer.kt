package pl.gov.anna.backend.domain

import io.reactivex.Single
import pl.gov.anna.backend.api.RegistrationResponse
import pl.gov.anna.backend.api.RegistrationService
import pl.gov.anna.information.Session

class AnnaServer(
    private val registrationService: RegistrationService,
    private val session: Session
) {

    fun initRegistration(msisdn: String): Single<RegistrationResponse> =
        registrationService
        .initRegistration(msisdn)
        .doOnSubscribe { session.initRegistration(msisdn) }
        .doOnSuccess { session.save(it) }

    fun confirmRegistration(code: String) =
        Single.just(session.isActiveRegistrationProcess)
        .flatMap { registrationService.confirmRegistration(code, session.registrationId) }
        .doOnSuccess { session.registered(it) }
}