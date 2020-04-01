package pl.gov.mc.protego.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.ui.observeLiveData

class SplashScreenActivity : AppCompatActivity() {

    private val viewModel: SplashScreenViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.apply {
            observeLiveData(targetScreen) {
                startActivity(Intent(this@SplashScreenActivity, it))
                finish()
            }
            fetchTargetScreen()
        }
    }
}