package pl.gov.mc.protegosafe.data.repository

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.concurrent.TimeUnit
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.data.extension.toCompletable
import pl.gov.mc.protegosafe.data.extension.toSingle
import pl.gov.mc.protegosafe.domain.exception.NoInternetConnectionException
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.repository.DiagnosisKeyRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository
import pl.gov.mc.protegosafe.domain.usecase.DiagnosisKeysFileNameToTimestampUseCase
import timber.log.Timber

class DiagnosisKeyRepositoryImpl(
    private val context: Context,
    private val firebaseStorage: FirebaseStorage,
    private val remoteConfigurationRepository: RemoteConfigurationRepository,
    private val internetConnectionManager: InternetConnectionManager,
    sharedPreferencesDelegates: SharedPreferencesDelegates
) : DiagnosisKeyRepository {

    companion object {
        private const val DIAGNOSIS_KEYS_DOWNLOAD_DIRECTORY = "DiagnosisKeys"
        private const val LATEST_PROCESSED_DIAGNOSIS_KEY_TIMESTAMP_PREF_KEY =
            "LATEST_PROCESSED_DIAGNOSIS_KEY_TIMESTAMP_PREF_KEY"
    }

    private val _downloadDirectory = initDownloadDirectory()
    private var _latestProcessedDiagnosisKeyTimestamp by sharedPreferencesDelegates.longPref(
        LATEST_PROCESSED_DIAGNOSIS_KEY_TIMESTAMP_PREF_KEY, 0
    )

    override fun getDiagnosisKeys(createdAfter: Long): Single<List<File>> {
        Timber.d("getDiagnosisKeys, createdAfter: $createdAfter")
        return getDiagnosisKeyDownloadConfiguration()
            .flatMap { downloadConfiguration ->
                val downloadedFileList = mutableListOf<File>()
                return@flatMap getDiagnosisKeysFilesStorageReferences(createdAfter = createdAfter)
                    .map { storageReferences ->
                        val pendingDownloadCompletable = arrayListOf<Completable>()
                        storageReferences.forEach { storageReference ->
                            require(storageReference.name.isNotBlank())
                            val file = File(_downloadDirectory, storageReference.name)
                            if (file.exists()) {
                                downloadedFileList.add(file)
                            } else {
                                pendingDownloadCompletable.add(
                                    storageReference.getFile(file).toCompletable()
                                        .timeout(
                                            downloadConfiguration.timeout,
                                            downloadConfiguration.timeoutUnit
                                        )
                                        .retry(downloadConfiguration.retryCount)
                                        .doOnComplete { downloadedFileList.add(file) }
                                )
                            }
                        }
                        return@map pendingDownloadCompletable
                    }
                    .flatMapCompletable { completables ->
                        Completable.merge(completables)
                    }
                    .toSingleDefault(downloadedFileList)
            }
    }

    override fun setLatestProcessedDiagnosisKeyTimestamp(timestamp: Long) {
        Timber.d("setLatestProcessedDiagnosisKeyTimestamp")
        _latestProcessedDiagnosisKeyTimestamp = timestamp
    }

    override fun getLatestProcessedDiagnosisKeyTimestamp(): Long {
        Timber.d("getLatestProcessedDiagnosisKeyTimestamp")
        return _latestProcessedDiagnosisKeyTimestamp
    }

    private fun getDiagnosisKeysFilesStorageReferences(createdAfter: Long):
            Single<List<StorageReference>> {
        Timber.d("getDiagnosisKeysFilesStorageReferences")
        return getDiagnosisKeysFilesListResult()
            .observeOn(Schedulers.io())
            .map { listResult ->
                return@map listResult.items.filter { item ->
                    require(item.name.isNotBlank())
                    DiagnosisKeysFileNameToTimestampUseCase().execute(item.name)
                        ?.let { fileTimestamp ->
                            require(fileTimestamp > 0)
                            fileTimestamp > createdAfter
                        } ?: false
                }
            }
    }

    private fun getDiagnosisKeysFilesListResult(): Single<ListResult> {
        Timber.d("getDiagnosisKeysFilesListResult")
        return firebaseStorage.reference.listAll().toSingle()
    }

    private fun initDownloadDirectory(): File {
        val downloadDirectory = File(context.filesDir, DIAGNOSIS_KEYS_DOWNLOAD_DIRECTORY)
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdir()
        }
        return downloadDirectory
    }

    private fun getDiagnosisKeyDownloadConfiguration(): Single<DownloadConfiguration> {
        return remoteConfigurationRepository.update()
            .andThen(remoteConfigurationRepository.getDiagnosisKeyDownloadConfiguration())
            .map { remoteConfiguration ->
                val timeout = when (internetConnectionManager.getInternetConnectionStatus()) {
                    InternetConnectionManager.InternetConnectionStatus.NONE ->
                        throw NoInternetConnectionException()
                    InternetConnectionManager.InternetConnectionStatus.MOBILE_DATA ->
                        remoteConfiguration.timeoutMobileSeconds
                    InternetConnectionManager.InternetConnectionStatus.WIFI ->
                        remoteConfiguration.timeoutWifiSeconds
                }

                return@map DownloadConfiguration(
                    timeout = timeout,
                    timeoutUnit = TimeUnit.SECONDS,
                    retryCount = remoteConfiguration.retryCount
                )
            }
    }

    private data class DownloadConfiguration(
        val timeout: Long,
        val retryCount: Long,
        val timeoutUnit: TimeUnit
    )
}
