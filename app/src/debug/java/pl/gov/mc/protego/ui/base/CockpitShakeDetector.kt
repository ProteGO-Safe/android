package pl.gov.mc.protego.ui.base

import android.hardware.SensorManager
import com.squareup.seismic.ShakeDetector

class CockpitShakeDetector {
    private var onShake: () -> Unit = {}

    private val shakeDetector = ShakeDetector { onShake() }.apply { setSensitivity(10) }

    fun startDetection(sensorManager: SensorManager) {
        shakeDetector.start(sensorManager)
    }

    fun stopDetection() {
        shakeDetector.stop()
    }

    fun setListener(listener: () -> Unit) {
        onShake = listener
    }

    fun removeListener() {
        onShake = {}
    }
}