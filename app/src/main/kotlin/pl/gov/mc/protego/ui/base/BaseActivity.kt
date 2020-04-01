package pl.gov.mc.protego.ui.base

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.polidea.cockpit.cockpit.Cockpit
import com.squareup.seismic.ShakeDetector
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity(), ShakeDetector.Listener {
    private val shakeDetector = ShakeDetector(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initShakeDetection()
    }

    override fun onResume() {
        super.onResume()
        Cockpit.addTestCockpitActionRequestCallback({ lifecycle }) { Timber.d("Cockpit test run") }
    }

    override fun onStop() {
        super.onStop()
        shakeDetector.stop()
    }

    private fun initShakeDetection() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector.start(sensorManager)
    }

    override fun hearShake() {
        Cockpit.showCockpit(supportFragmentManager)
    }
}