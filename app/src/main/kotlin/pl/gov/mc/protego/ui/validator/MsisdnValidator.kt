package pl.gov.mc.protego.ui.validator

import java.lang.IllegalArgumentException

class InvalidMsisdnException(message: String) : IllegalArgumentException(message)

sealed class MsisdnValidationResult
object MsisdnIncomplete : MsisdnValidationResult()
object MsisdnInvalid : MsisdnValidationResult()
object MsisdnOk : MsisdnValidationResult()

class MsisdnValidator {
    // https://pl.wikipedia.org/wiki/Numery_telefoniczne_w_Polsce#Sieci_ruchome_(komórkowe)
    var prefixes = listOf(50, 51, 53, 57, 60, 66, 69, 72, 73, 78, 79, 88).map{it.toString()}

    fun validate(msisdn: String) =
        when {
            msisdn.length == 9 ->
                if (prefixes.any{msisdn.startsWith(it)}) MsisdnOk else MsisdnInvalid
            msisdn.length < 9 -> MsisdnIncomplete
            else -> MsisdnInvalid
        }
    fun validateWithCountryCode(msisdn: String) = msisdn.length == 12
}