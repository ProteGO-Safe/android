package pl.gov.mc.protego.backend.api

import io.reactivex.Single
import pl.gov.mc.protego.ui.validator.InvalidMsisdnException
import pl.gov.mc.protego.ui.validator.MsisdnValidator

enum class MsisdnCountryCode(val code: String) {
    PL("+48")
}

class RegistrationService(
    private val registrationAPI: RegistrationAPI,
    private val msisdnValidator: MsisdnValidator,
    private val requestComposer: RegistrationRequestComposer
) {
    fun registerAnonymously(): Single<AnonymousRegistrationResponse> =
        Single.just(requestComposer.anonymousRegistration())
            .flatMap { registrationAPI.registerAnonymously(it) }

    fun initRegistration(msisdn: String): Single<RegistrationResponse> =
        Single.just(msisdn.withCountryCode(MsisdnCountryCode.PL))
            .doOnSuccess {
                if (!msisdnValidator.validateWithCountryCode(it)) {
                    throw InvalidMsisdnException("$it is not a valid MSISDN")
                }
            }
            .map { requestComposer.init(it) }
            .flatMap { registrationAPI.register(it) }

    fun confirmRegistration(code: String, registrationId: String) =
        Single.just(code)
            .map { requestComposer.confirm(code, registrationId) }
            .flatMap { registrationAPI.confirmRegistration(it) }
}

private fun String.withCountryCode(countryCode: MsisdnCountryCode): String =
    "${countryCode.code}$this"
