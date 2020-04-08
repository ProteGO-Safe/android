package pl.gov.mc.protego.repository

import android.content.SharedPreferences
import pl.gov.mc.protego.information.SessionData
import pl.gov.mc.protego.information.SessionState
import timber.log.Timber

class SessionRepository(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        const val USER_ID = "USER_ID"
        const val STATE = "STATE"
        const val MSISDN = "MSISDN"
    }

    fun store(session: SessionData) {
        Timber.d("Store session data: ${session}")
        sharedPreferences.edit().run {
            putString(USER_ID, session.userId)
            putString(STATE, session.state.checkpoint.name)
            putString(MSISDN, session.msisdn)
            apply()
        }
    }

    fun restore(): SessionData {
        return SessionData(
            state = SessionState.byName(
                sharedPreferences.getString(
                    STATE,
                    null
                )
            ),
            msisdn = sharedPreferences.getString(
                MSISDN,
                null
            ),
            userId = sharedPreferences.getString(
                USER_ID,
                null
            )
        )
    }
}