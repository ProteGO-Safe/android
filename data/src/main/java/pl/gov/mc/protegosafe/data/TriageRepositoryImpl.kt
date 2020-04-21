package pl.gov.mc.protegosafe.data

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.db.TriageDataStore
import pl.gov.mc.protegosafe.domain.repository.TriageRepository
import pl.gov.mc.protegosafe.domain.model.TriageData
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