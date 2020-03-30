package pl.gov.mc.protego.backend.api

import pl.gov.mc.protego.information.AppInformation
import pl.gov.mc.protego.information.PhoneInformation
import pl.gov.mc.protego.information.Session

class RegistrationRequestComposer(
    private val requestComposer: RequestComposer
) {
    fun init(msisdn: String) = RegistrationRequest(
        msisdn,
        requestComposer.standardRequestData
    )
    fun confirm(code: String, registrationId: String) =
        ConfirmRegistrationRequest(
            confirmationCode = code,
            registrationId = registrationId,
            standardRequestData = requestComposer.standardRequestData
        )
}

class RequestComposer(
    val phoneInformation: PhoneInformation,
    val appInformation: AppInformation,
    val session: Session
) {
    val standardRequestData: StandardRequestData
        get() {
            return StandardRequestData(
                platform = phoneInformation.platform,
                osVersion = phoneInformation.osVersion,
                deviceName = phoneInformation.deviceName,
                appVersion = appInformation.version,
                apiVersion = appInformation.api,
                lang = phoneInformation.lang,
                userId = session.userId
            )
        }
}