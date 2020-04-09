package pl.gov.mc.protego.ui.validator

import androidx.annotation.StringRes
import pl.gov.mc.protego.R

class InvalidMsisdnException(message: String) : IllegalArgumentException(message)

sealed class MsisdnValidationResult(val success: Boolean, @StringRes val errorMessage: Int? = null)
object MsisdnIncomplete : MsisdnValidationResult(false, R.string.registration_phone_number_incomplete)
object MsisdnInvalid : MsisdnValidationResult(false, R.string.registration_phone_number_invalid)
object MsisdnOk : MsisdnValidationResult(true)

class MsisdnValidator {

    fun validate(msisdn: String) =
        when {
            msisdn.length < 9 -> MsisdnIncomplete
            msisdn.length == 9 -> MsisdnOk
            else -> MsisdnInvalid
        }

    fun validateWithCountryCode(msisdn: String) = msisdn.length == 12
}