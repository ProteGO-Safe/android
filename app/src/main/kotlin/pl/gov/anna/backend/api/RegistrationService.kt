package pl.gov.anna.backend.api

import io.reactivex.Single
import pl.gov.anna.ui.validator.InvalidMsisdnException
import pl.gov.anna.ui.validator.MsisdnValidator

enum class MsisdnCountryCode(val code: String) {
    PL("+48")
}

class RegistrationService (
    private val registrationAPI: RegistrationAPI,
    private val msisdnValidator: MsisdnValidator,
    private val requestComposer: RegistrationRequestComposer
    ) {

    fun startRegistration(msisdn: String): Single<RegistrationResponse> =
        Single.just(msisdn.withCountyCode(MsisdnCountryCode.PL))
        .doOnSuccess { if(!msisdnValidator.validateWithCountryCode(it)) {
            throw InvalidMsisdnException("$it is not a valid MSISDN")
        } }
        .map { requestComposer(it) }
        .flatMap { registrationAPI.register(it) }
}

private fun String.withCountyCode(countryCode: MsisdnCountryCode): String
    = "${countryCode.code}$this"
