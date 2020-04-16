package se.sigmaconnectivity.blescanner.domain

import se.sigmaconnectivity.blescanner.domain.model.TriageData

interface TriageRepository {
    fun getLastTriageCompletedTimestamp(): Long
    fun saveTriageCompletedTimestamp(timestamp: Long)
    fun parseBridgePayload(payload: String): TriageData
}