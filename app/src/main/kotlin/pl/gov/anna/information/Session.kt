package pl.gov.anna.information

import pl.gov.anna.backend.api.ConfirmationRegistrationResponse
import pl.gov.anna.backend.api.RegistrationResponse
import java.lang.IllegalStateException

enum class SessionState {
    UNREGISTERED,
    REGISTRATION,
    IDLE
}

class Session {

    private var sessionState = SessionState.UNREGISTERED
     @Synchronized get
     @Synchronized set

    private var msisdn: String? = null
    var userId: String? = null
        private set

    fun initRegistration(msisdn: String) {
        sessionState = SessionState.REGISTRATION
        this.msisdn = msisdn
    }

    fun registered(confirmationRegistrationResponse: ConfirmationRegistrationResponse) {
        sessionState = SessionState.IDLE
        userId = confirmationRegistrationResponse.userId
    }

    fun save(registrationResponse: RegistrationResponse) {
        _registrationId = registrationResponse.registrationId
    }

    private var _registrationId: String? = null
    val registrationId: String
        get() = if (!_registrationId.isNullOrEmpty()) _registrationId!! else throw IllegalStateException("RegistrationId is not defined.")

    val isActiveRegistrationProcess: Boolean
        get()= sessionState == SessionState.REGISTRATION
}