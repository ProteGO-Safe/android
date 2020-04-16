package se.sigmaconnectivity.blescanner.domain

interface TriageRepository {
    fun getLastTriageCompletedTimestamp(): Long
    fun saveTriageCompletedTimestamp(timestamp: Long)
}