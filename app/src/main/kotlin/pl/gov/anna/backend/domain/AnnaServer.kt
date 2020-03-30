package pl.gov.anna.backend.domain

import io.reactivex.Single
import pl.gov.anna.backend.api.RegistrationService
import pl.gov.anna.information.Session
import pl.gov.anna.information.SessionData

class AnnaServer(
    private val registrationService: RegistrationService,
    private val session: Session
) {

    fun initRegistration(msisdn: String): Single<SessionData> =
        registrationService
        .initRegistration(msisdn)
        .doOnSubscribe { session.initRegistration(msisdn) }
        .flatMap { session.save(it) }

    fun confirmRegistration(code: String) =
        Single.just(session.isActiveRegistrationProcess)
        .flatMap { registrationService.confirmRegistration(code, session.registrationId) }
        .flatMap { session.registered(it) }
}