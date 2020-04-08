package pl.gov.mc.protego.information

import io.reactivex.Single
import pl.gov.mc.protego.backend.api.ConfirmationRegistrationResponse
import pl.gov.mc.protego.backend.api.RegistrationResponse
import pl.gov.mc.protego.repository.SessionRepository
import java.lang.IllegalStateException

enum class SessionState {

    UNREGISTERED,
    REGISTRATION,
    LOGGED_IN;

    val checkpoint: SessionState
        get() = when(this){
            UNREGISTERED -> UNREGISTERED
            REGISTRATION -> UNREGISTERED
            LOGGED_IN -> LOGGED_IN
        }

    companion object {
        fun byName(stateName: String?) = SessionState.values().firstOrNull { it.name == stateName } ?: UNREGISTERED
    }
}

enum class EmergencyState { RED, ORANGE, GREEN }

data class SessionData(
    val state: SessionState,
    val userId: String? = null,
    val registrationId: String? = null,
    val msisdn: String? = null,
    val emergencyState: EmergencyState = EmergencyState.ORANGE,
    val debugCode : String? = null
)

class Session(
    private val sessionRepository: SessionRepository
) {

    var sessionData =
        SessionData(SessionState.UNREGISTERED)
        @Synchronized get
        @Synchronized private set

    private var msisdn: String? = null
    val userId: String?
        get() = sessionData.userId

    init {
        sessionData = sessionRepository.restore()
    }

    fun initRegistration(msisdn: String) {
        sessionData = sessionData.copy(state = SessionState.REGISTRATION)
        this.msisdn = msisdn
    }

    fun registered(confirmationRegistrationResponse: ConfirmationRegistrationResponse): Single<SessionData> {
        return Single.create<SessionData> {
            sessionData = sessionData.copy(state = SessionState.LOGGED_IN, userId = confirmationRegistrationResponse.userId)
            sessionRepository.store(sessionData)
            it.onSuccess(sessionData)
        }
    }

    fun save(registrationResponse: RegistrationResponse): Single<SessionData> {
        return Single.create<SessionData> {
            sessionData = sessionData.copy(registrationId = registrationResponse.registrationId, debugCode = registrationResponse.code)
            sessionRepository.store(sessionData)
            it.onSuccess(sessionData)
        }
    }

    fun logout() {
        sessionData =
            SessionData(state = SessionState.UNREGISTERED)
        sessionRepository.store(sessionData)
    }

    val registrationId: String
        get() = sessionData.registrationId ?: throw IllegalStateException("RegistrationId is not defined.")

    val isActiveRegistrationProcess: Boolean
        get()= sessionData.state == SessionState.REGISTRATION
}