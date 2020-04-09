package pl.gov.mc.protego.backend.domain

import io.reactivex.Single
import pl.gov.mc.protego.backend.api.RegistrationService
import pl.gov.mc.protego.backend.api.StatusService
import pl.gov.mc.protego.information.EmergencyState
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.information.SessionData

class ProtegoServer(
    private val registrationService: RegistrationService,
    private val statusService: StatusService,
    private val session: Session
) {
    fun registerAnonymously(): Single<SessionData> =
        registrationService.registerAnonymously().flatMap { session.registered(it) }

    fun registerWithPhoneNumber(msisdn: String): Single<SessionData> =
        registrationService
            .initRegistration(msisdn)
            .doOnSubscribe { session.initRegistration(msisdn) }
            .flatMap { session.save(it) }

    fun confirmRegistration(code: String) =
        Single.just(session.isActiveRegistrationProcess)
            .flatMap { registrationService.confirmRegistration(code, session.registrationId) }
            .flatMap { session.registered(it) }

    fun fetchState(): Single<EmergencyState> =
        Single.just("SampleData")
            .flatMap { statusService.fetchStatusAndIds(it) }
            .map { EmergencyState.ORANGE }

}