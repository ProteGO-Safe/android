package pl.gov.mc.protego.util

object SmsParser {

    private val regex = Regex("\\d{6,}")

    fun getCodeFromSms(sms: String): String? {
        return regex.find(sms)?.value
    }
}