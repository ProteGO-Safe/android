package pl.gov.anna.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import pl.gov.anna.R
import pl.gov.anna.information.Session
import pl.gov.anna.ui.registration.RegistrationActivity

class MainActivity : AppCompatActivity() {

    private val session: Session by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logout_button.setOnClickListener {
            session.logout()
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }
}
