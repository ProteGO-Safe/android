package pl.gov.anna.ui.validator

import java.lang.IllegalArgumentException


class InvalidMsisdnException(message: String) : IllegalArgumentException(message)

class MsisdnValidator {
    fun validate(msisdn: String) = msisdn.length == 9
    fun validateWithCountryCode(msisdn: String) = msisdn.length == 12
}