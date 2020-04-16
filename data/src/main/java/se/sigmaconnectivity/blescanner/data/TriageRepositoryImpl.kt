package se.sigmaconnectivity.blescanner.data

import com.google.gson.Gson
import se.sigmaconnectivity.blescanner.data.db.TriageDataStore
import se.sigmaconnectivity.blescanner.domain.TriageRepository
import se.sigmaconnectivity.blescanner.domain.model.TriageData
import timber.log.Timber

class TriageRepositoryImpl(
    private val triageDataStore: TriageDataStore
): TriageRepository {
    override fun getLastTriageCompletedTimestamp(): Long {
        return triageDataStore.lastTriageCompletedTimestamp
    }

    override fun saveTriageCompletedTimestamp(timestamp: Long) {
        Timber.d("Triage completed timestamp: $timestamp")
        triageDataStore.lastTriageCompletedTimestamp = timestamp
    }

    override fun parseBridgePayload(payload: String): TriageData {
        Timber.d("Parsing payload: $payload")
        return Gson().fromJson(payload, TriageData::class.java)
    }
}