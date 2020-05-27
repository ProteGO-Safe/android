package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Single
import java.io.File

interface DiagnosisKeyRepository {
    /**
     * Get Diagnosis Keys Files
     *
     * Returns list of Diagnosis Keys files created after [createdAfter] Unix epoch time.
     *
     * @param createdAfter Unix epoch time in seconds, provides that only files created after this
     * time will being returned.
     * @return [Single] with list of files containing Diagnosis Keys.
     */
    fun getDiagnosisKeys(createdAfter: Long = 0L): Single<List<File>>

    /**
     * Sets the latest processed Diagnosis Key file timestamps.
     *
     * @param timestamp Unix epoch time in seconds
     */
    fun setLatestProcessedDiagnosisKeyTimestamp(timestamp: Long)

    /**
     * Gets the latest processed Diagnosis Key file timestamps.
     *
     *  @return The latest processed Diagnosis Key file timestamps in Unix epoch time in seconds
     */
    fun getLatestProcessedDiagnosisKeyTimestamp(): Long
}
