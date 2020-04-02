package pl.gov.mc.protego.ui.base

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.polidea.cockpit.cockpit.Cockpit
import com.polidea.cockpit.event.ActionRequestCallback
import com.squareup.seismic.ShakeDetector
import org.koin.android.ext.android.inject
import pl.gov.mc.protego.R
import timber.log.Timber
import java.util.*

abstract class BaseActivity : AppCompatActivity() {
    private val shakeDetector: CockpitShakeDetector by inject()

    private val testCockpitAction: ActionRequestCallback = ActionRequestCallback { //TODO figure out something better
        Timber.d("Cockpit test run ${Date().time}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initShakeDetection()
    }

    override fun onStart() {
        super.onStart()
        initCockpit()

        findViewById<Toolbar>(R.id.toolbar)?.apply {
            setSupportActionBar(this)

            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        shakeDetector.stopDetection()
        deinitCockpit()
    }

    private fun initShakeDetection() {

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector.startDetection(sensorManager)
        shakeDetector.setListener { Cockpit.showCockpit(supportFragmentManager) }
    }

    private fun initCockpit() {
        Cockpit.addTestCockpitActionRequestCallback(this, testCockpitAction)
    }

    private fun deinitCockpit() {
        Cockpit.removeTestCockpitActionRequestCallback(testCockpitAction)
    }
}