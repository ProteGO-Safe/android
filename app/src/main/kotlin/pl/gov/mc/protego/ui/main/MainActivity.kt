package pl.gov.mc.protego.ui.main

import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.polidea.cockpit.cockpit.Cockpit
import com.squareup.seismic.ShakeDetector
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.ui.registration.RegistrationActivity
import timber.log.Timber

class MainActivity : AppCompatActivity(), ShakeDetector.Listener {

    private val viewModel: MainActivityViewModel by viewModel()
    private val session: Session by inject()
    private val shakeDetector = ShakeDetector(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logout_button.setOnClickListener {
            session.logout()
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
        initShakeDetection()
    }

    private fun initShakeDetection() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector.start(sensorManager)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        Cockpit.addTestCockpitActionRequestCallback({ lifecycle }) { Timber.d("Cockpit test run") }
    }

    override fun onStop() {
        super.onStop()
        shakeDetector.stop()
    }

    override fun hearShake() {
        Cockpit.showCockpit(supportFragmentManager)
    }
}
