package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Single
import pl.gov.mc.protegosafe.data.exposuresproto.TemporaryExposureKeyExport
import pl.gov.mc.protegosafe.domain.repository.ProtobufRepository
import timber.log.Timber
import java.io.File
import java.util.zip.ZipFile

class ProtobufRepositoryImpl : ProtobufRepository {

    override fun getTemporaryExposureKeysAmount(files: List<File>): Single<Long> {
        return Single.fromCallable {
            var counter = 0L
            files.forEach { file ->
                counter += getTemporaryExposureKeyExport(ZipFile(file)).keys.size
            }
            Timber.d("Amount of Temporary Exposure keys to analyze = $counter")
            return@fromCallable counter
        }
    }

    private fun getTemporaryExposureKeyExport(zipFile: ZipFile): TemporaryExposureKeyExport {
        val exportEntry = zipFile.getEntry("export.bin")
        val exportByteArray = zipFile.getInputStream(exportEntry)
            .readBytes()
            .drop(16)
            .toByteArray()
        return TemporaryExposureKeyExport.ADAPTER.decode(exportByteArray)
    }
}
