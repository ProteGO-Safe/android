package pl.gov.anna.ui.validator

class MsisdnValidator {
    fun validate(msisdn: String) = msisdn.length == 9
}