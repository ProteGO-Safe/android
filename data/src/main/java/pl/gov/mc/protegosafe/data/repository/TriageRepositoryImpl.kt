package pl.gov.mc.protegosafe.data.repository

import pl.gov.mc.protegosafe.data.db.TriageDataStore
import pl.gov.mc.protegosafe.domain.repository.TriageRepository
import timber.log.Timber

class TriageRepositoryImpl(
    private val triageDataStore: TriageDataStore
) : TriageRepository {
    override fun getLastTriageCompletedTimestamp(): Long {
        return triageDataStore.lastTriageCompletedTimestamp
    }

    override fun saveTriageCompletedTimestamp(timestamp: Long) {
        Timber.d("Triage completed timestamp: $timestamp")
        triageDataStore.lastTriageCompletedTimestamp = timestamp
    }
}
