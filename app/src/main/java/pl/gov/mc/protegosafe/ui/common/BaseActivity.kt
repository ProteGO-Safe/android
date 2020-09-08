package pl.gov.mc.protegosafe.ui.common

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.get
import pl.gov.mc.protegosafe.domain.usecase.GetLocaleUseCase

abstract class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(getLocaleContext(newBase))
    }

    private fun getLocaleContext(context: Context?): Context? {
        val locale = get<GetLocaleUseCase>().execute()
        val config = Configuration(context?.resources?.configuration).apply {
            setLocale(locale)
        }

        return context?.createConfigurationContext(config)
    }
}
