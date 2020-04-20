package pl.gov.mc.protegosafe.domain

import pl.gov.mc.protegosafe.domain.model.TriageData

interface TriageRepository {
    fun getLastTriageCompletedTimestamp(): Long
    fun saveTriageCompletedTimestamp(timestamp: Long)
    fun parseBridgePayload(payload: String): TriageData
}