package pl.gov.mc.protego.ui.registration

import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.polidea.cockpit.cockpit.Cockpit
import com.squareup.seismic.ShakeDetector
import kotlinx.android.synthetic.main.registration_confirmation_view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.ui.main.MainActivity
import timber.log.Timber
import pl.gov.mc.protego.ui.observeLiveData

class RegistrationConfirmationActivity : AppCompatActivity(), ShakeDetector.Listener {

    private val viewModel: RegistrationConfirmationViewModel by viewModel()
    private val shakeDetector = ShakeDetector(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_confirmation_view)

        confirm_registration_button.setOnClickListener {
            if (sms_code.text.toString().isNotEmpty()) {
                viewModel.confirm(sms_code.text.toString())
            } else {
                sms_code_layout.error = "Wpisz kod"
            }
        }

        observeLiveData(viewModel.confirmationError) {
            Toast.makeText(this, "Problem z rejestracją: $it", Toast.LENGTH_LONG).show()
        }

        observeLiveData(viewModel.confirmationSuccess) {
            navigateToMain()
        }

        initShakeDetection()
        Cockpit.addTestCockpitActionRequestCallback({ lifecycle }) { Timber.d("Cockpit test run") }
    }

    private fun initShakeDetection() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        shakeDetector.start(sensorManager)
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onStop() {
        super.onStop()
        shakeDetector.stop()
    }

    override fun hearShake() {
        Cockpit.showCockpit(supportFragmentManager)
    }
}