package pl.gov.mc.protego.ui.validator

import java.lang.IllegalArgumentException

class InvalidMsisdnException(message: String) : IllegalArgumentException(message)

sealed class MsisdnValidationResult
object MsisdnIncomplete : MsisdnValidationResult()
object MsisdnInvalid : MsisdnValidationResult()
object MsisdnOk : MsisdnValidationResult()

class MsisdnValidator {

    fun validate(msisdn: String) =
        when {
            msisdn.length < 9 -> MsisdnIncomplete
            msisdn.length == 9 -> MsisdnOk
            else -> MsisdnInvalid
        }
    fun validateWithCountryCode(msisdn: String) = msisdn.length == 12
}