package pl.gov.anna.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.registration_confirmation_view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.anna.R
import pl.gov.anna.ui.main.MainActivity

class RegistrationConfirmationActivity : AppCompatActivity() {

    private val viewModel: RegistrationConfirmationViewModel by viewModel()

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

        viewModel.confirmationError.addObserver {
            Toast.makeText(this, "Problem z rejestracją: $it", Toast.LENGTH_LONG).show()
        }

        viewModel.confirmationSuccess.addObserver {
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun <T> MutableLiveData<T>.addObserver(observer: (T) -> Unit) {
        observe(this@RegistrationConfirmationActivity, Observer { observer(it) })
    }
}