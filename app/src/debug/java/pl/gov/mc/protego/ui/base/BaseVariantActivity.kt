package pl.gov.mc.protego.ui.base

import android.content.Context
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject

abstract class BaseVariantActivity : AppCompatActivity() {
    private val shakeDetector: CockpitShakeDetector by inject()

    override fun onStart() {
        super.onStart()
        startShakeDetection()
    }

    override fun onStop() {
        super.onStop()
        stopShakeDetection()
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