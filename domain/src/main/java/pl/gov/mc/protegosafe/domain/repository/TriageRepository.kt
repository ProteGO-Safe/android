package pl.gov.mc.protegosafe.domain.repository

interface TriageRepository {
    fun getLastTriageCompletedTimestamp(): Long
    fun saveTriageCompletedTimestamp(timestamp: Long)
}
