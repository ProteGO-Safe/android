package pl.gov.mc.protegosafe

import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.github.takahirom.hyperion.plugin.simpleitem.SimpleItem
import com.github.takahirom.hyperion.plugin.simpleitem.SimpleItemHyperionPlugin
import io.reactivex.rxkotlin.subscribeBy
import org.koin.android.ext.android.inject
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.domain.DumpTraceDataUseCase
import timber.log.Timber

fun App.initializeHyperionDebugMenu(dumpTraceDataUseCase: DumpTraceDataUseCase) {
    if (BuildConfig.DEBUG) {
        val item = SimpleItem.Builder("Dump trace JSON")
            .text("Dumb all the data gathered by OpenTrace module to JSON file")
            .image(R.drawable.ic_file_download_gray_24dp)
            .clickListener{ _ ->
                Toast.makeText(this, "Dumping data", Toast.LENGTH_SHORT).show()
                dumpTraceDataUseCase.execute(DEBUG_UPLOAD_TOKEN)
                    .subscribeBy(
                        onSuccess = {file ->
                            Toast.makeText(this, "Data successfully dumped", Toast.LENGTH_SHORT).show()
                            if (file.exists()) {
                                val fileUri = FileProvider.getUriForFile(
                                    this,
                                    applicationContext.packageName + ".fileprovider",
                                    file
                                )

                                val intent = Intent(Intent.ACTION_SEND)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.type = "text/json"
                                intent.putExtra(
                                    Intent.EXTRA_STREAM,
                                    fileUri
                                )
                                val chooserIntent = Intent.createChooser(intent, "Share JSON dump file")
                                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(chooserIntent)
                            }
                        },
                        onError= {
                            Timber.e(it)
                            Toast.makeText(this, "Couldn't dump data", Toast.LENGTH_SHORT).show()
                        }
                    )
            }
            .build()
        SimpleItemHyperionPlugin.addItem(item)
    }
}

private const val DEBUG_UPLOAD_TOKEN = "<upload token goes here>"