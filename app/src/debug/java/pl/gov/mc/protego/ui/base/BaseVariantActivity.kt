package pl.gov.mc.protego.ui.base

import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import com.polidea.cockpit.cockpit.Cockpit
import com.polidea.cockpit.event.ActionRequestCallback
import org.koin.android.ext.android.inject
import org.koin.core.context.KoinContextHandler
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.information.SessionState
import pl.gov.mc.protego.ui.registration.onboarding.OnboardingActivity

abstract class BaseVariantActivity : AppCompatActivity() {
    private val shakeDetector: CockpitShakeDetector by inject()
    private val logoutCallback: ActionRequestCallback = ActionRequestCallback {
        val session: Session = KoinContextHandler.get().get()
        if (session.sessionData.state == SessionState.LOGGED_IN) {
            session.logout()
            startActivity(Intent(this, OnboardingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        startShakeDetection()
        initCockpit()
    }

    override fun onStop() {
        super.onStop()
        stopShakeDetection()
        deinitCockpit()
    }

    private fun initCockpit() {
        Cockpit.addLogoutActionRequestCallback(this, logoutCallback)
    }

    private fun deinitCockpit() {
        Cockpit.removeLogoutActionRequestCallback(logoutCallback)
    }

    private fun startShakeDetection() {
        (getSystemService(Context.SENSOR_SERVICE) as? SensorManager)?.let { sensorManager ->
            shakeDetector.apply {
                startDetection(sensorManager)
                setListener { CockpitMenuLauncher.showCockpit(supportFragmentManager) }
            }
        }
    }

    private fun stopShakeDetection() {
        shakeDetector.apply {
            stopDetection()
            removeListener()
        }
    }
}