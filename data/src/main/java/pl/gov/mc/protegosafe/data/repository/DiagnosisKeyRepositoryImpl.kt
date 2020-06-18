package pl.gov.mc.protegosafe.data.repository

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.cloud.DiagnosisKeyDownloadService
import pl.gov.mc.protegosafe.data.cloud.downloadToFile
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.domain.exception.NoInternetConnectionException
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.repository.DiagnosisKeyRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository
import pl.gov.mc.protegosafe.domain.usecase.DiagnosisKeysFileNameToTimestampUseCase
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class DiagnosisKeyRepositoryImpl(
    private val context: Context,
    private val diagnosisKeyDownloadService: DiagnosisKeyDownloadService,
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
            .subscribeOn(Schedulers.io())
            .flatMap { downloadConfiguration ->
                val downloadedFileList = mutableListOf<File>()
                return@flatMap getFilteredDiagnosisKeysFilesNames(createdAfter = createdAfter)
                    .map { diagnosisFileNames ->
                        val pendingDownloadCompletable = arrayListOf<Completable>()
                        diagnosisFileNames.forEach { diagnosisFileName ->
                            // diagnosisFileName contains redundant "/" suffix
                            val fileName = diagnosisFileName.removePrefix("/")
                            require(fileName.isNotBlank())

                            val file = File(_downloadDirectory, fileName)
                            if (file.exists()) {
                                downloadedFileList.add(file)
                            } else {
                                pendingDownloadCompletable.add(
                                    diagnosisKeyDownloadService.downloadToFile(
                                        diagnosisFileName,
                                        file
                                    )
                                        .timeout(
                                            downloadConfiguration.timeout,
                                            downloadConfiguration.timeoutUnit
                                        )
                                        .retry(downloadConfiguration.retryCount)
                                        .doOnComplete { downloadedFileList.add(file) }
                                        .onErrorResumeNext {
                                            try {
                                                file.delete()
                                            } catch (e: Exception) {
                                                Timber.e(e, "Couldn't delete DK file.")
                                            }
                                            Completable.complete()
                                        }
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

    private fun getFilteredDiagnosisKeysFilesNames(createdAfter: Long):
            Single<List<String>> {
        Timber.d("getDiagnosisKeysFilesStorageReferences")

        return getDiagnosisKeysFiles()
            .observeOn(Schedulers.io())
            .map { listResult ->
                return@map listResult.filter { item ->
                    require(item.isNotBlank())
                    DiagnosisKeysFileNameToTimestampUseCase().execute(item)
                        ?.let { fileTimestamp ->
                            require(fileTimestamp > 0)
                            fileTimestamp > createdAfter
                        } ?: false
                }
            }
    }

    private fun getDiagnosisKeysFiles(): Single<List<String>> {
        Timber.d("getDiagnosisKeysFilesListResult")
        return diagnosisKeyDownloadService.getIndex()
            .subscribeOn(Schedulers.io())
            .flatMap { responseBody ->
                return@flatMap Single.just(responseBody.string().split("\n"))
            }
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
                    InternetConnectionManager.InternetConnectionStatus.MOBILE_DATA,
                    InternetConnectionManager.InternetConnectionStatus.VPN ->
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
