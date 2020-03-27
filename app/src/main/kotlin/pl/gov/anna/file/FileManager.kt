package pl.gov.anna.file

import android.content.Context
import android.content.Context.MODE_PRIVATE
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream


class FileManager(
    val context: Context
){

    fun saveSampleFile(fileName: String, content: String): Single<File> {

        return Single.create { emitter ->
            val outputStream: FileOutputStream

            val internalDir = context.filesDir
            val file = File(internalDir, fileName)

            try {
                outputStream = file.outputStream()
                outputStream.write(content.toByteArray())
                outputStream.flush()
                outputStream.close()
                emitter.onSuccess(file)
            } catch (e: Exception) {
                Timber.e(e, "Cannot save sample file")
                emitter.onError(e)

            }
        }
    }
}