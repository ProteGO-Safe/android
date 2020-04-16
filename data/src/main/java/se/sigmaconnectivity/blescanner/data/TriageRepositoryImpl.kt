package se.sigmaconnectivity.blescanner.data

import se.sigmaconnectivity.blescanner.data.db.TriageDataStore
import se.sigmaconnectivity.blescanner.domain.TriageRepository

class TriageRepositoryImpl(
    private val triageDataStore: TriageDataStore
): TriageRepository {
    override fun getLastTriageCompletedTimestamp(): Long {
        return triageDataStore.lastTriageCompletedTimestamp
    }

    override fun saveTriageCompletedTimestamp(timestamp: Long) {
        triageDataStore.lastTriageCompletedTimestamp = timestamp
    }
}