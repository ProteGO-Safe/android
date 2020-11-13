package pl.gov.mc.protegosafe

import android.content.Intent
import com.github.takahirom.hyperion.plugin.simpleitem.SimpleItem
import com.github.takahirom.hyperion.plugin.simpleitem.SimpleItemHyperionPlugin
import org.koin.core.context.loadKoinModules
import pl.gov.mc.protegosafe.di.helpers
import pl.gov.mc.protegosafe.ui.TestHelpersActivity

class AppWithHelpers : App() {

    override fun onCreate() {
        super.onCreate()

        loadKoinModules(helpers)

        initializeHyperion()
    }

    private fun initializeHyperion() {
        val testHelpersIntent = Intent(this, TestHelpersActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val item = SimpleItem.Builder("Open test helpers")
            .image(R.drawable.hammer_wrench)
            .clickListener { startActivity(testHelpersIntent) }
            .build()

        SimpleItemHyperionPlugin.addItem(item)
    }
}
