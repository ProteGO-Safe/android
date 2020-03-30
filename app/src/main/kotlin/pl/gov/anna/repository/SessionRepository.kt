package pl.gov.anna.repository

import android.content.SharedPreferences
import pl.gov.anna.information.SessionData
import pl.gov.anna.information.SessionState

class SessionRepository(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val USER_ID = "USER_ID"
        const val STATE = "STATE"
        const val MSISDN = "MSISDN"
    }

    fun store(session: SessionData) {
        sharedPreferences.edit().run {
            putString(USER_ID, session.userId)
            putString(STATE, session.state.name)
            putString(MSISDN, session.msisdn)
            apply()
        }
    }

    fun restore(): SessionData {
        return SessionData(
            state = SessionState.byName(sharedPreferences.getString(STATE, null)),
            msisdn = sharedPreferences.getString(MSISDN, null),
            userId = sharedPreferences.getString(USER_ID, null)
        )
    }
}