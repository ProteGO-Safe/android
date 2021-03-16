package pl.gov.mc.protegosafe.repository

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.FileRepository
import java.io.File

class FileRepositoryImpl(private val context: Context) : FileRepository {

    /**
     * Writes text to file in internal storage
     *
     * @param fileName File name
     * @param text Text to write
     */
    override fun writeInternalFile(fileName: String, text: String): Completable {
        return Completable.fromAction {
            val file = File(context.filesDir.path + "/$fileName")
            if (file.exists().not()) {
                file.createNewFile()
            }
            file.writeText(text)
        }
    }

    /**
     * Reads text from file in internal storage
     *
     * @param fileName File name
     * @return File content
     */
    override fun readInternalFile(fileName: String): Single<String> {
        return Single.fromCallable {
            val file = File(context.filesDir.path + "/$fileName")
            file.readText()
        }
    }

    /**
     * Reads text from file in internal storage or returns empty string on error (eg. missing file)
     *
     * @param fileName File name
     * @return File content or empty string
     */
    override fun readInternalFileOrEmpty(fileName: String): Single<String> {
        return readInternalFile(fileName).onErrorReturn { "" }
    }
}
