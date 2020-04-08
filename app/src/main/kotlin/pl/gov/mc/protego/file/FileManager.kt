package pl.gov.mc.protego.file

import android.content.Context
import io.reactivex.Single
import timber.log.Timber
import java.io.File


class FileManager(
    val context: Context
) {

    fun saveSampleFile(fileName: String, content: String): Single<File> {

        return Single.create { emitter ->
            val file = File(context.filesDir, fileName)

            try {
                file.writeText(content)
                emitter.onSuccess(file)
            } catch (e: Exception) {
                Timber.e(e, "Cannot save sample file")
                emitter.onError(e)
            }
        }
    }
}