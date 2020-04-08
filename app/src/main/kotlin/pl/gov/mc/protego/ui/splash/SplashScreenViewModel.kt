package pl.gov.mc.protego.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.information.SessionState
import pl.gov.mc.protego.ui.main.DashboardActivity
import pl.gov.mc.protego.ui.registration.onboarding.OnboardingActivity

class SplashScreenViewModel(
    private val session: Session
): ViewModel() {

    val targetScreen = MutableLiveData<Class<*>>()

    fun fetchTargetScreen() {
        targetScreen.value = when(session.sessionData.state.checkpoint) {
            SessionState.LOGGED_IN -> DashboardActivity::class.java
            else -> OnboardingActivity::class.java
        }
    }

}