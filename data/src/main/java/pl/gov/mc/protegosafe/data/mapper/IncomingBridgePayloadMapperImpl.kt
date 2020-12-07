package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.AppLanguageData
import pl.gov.mc.protegosafe.data.model.AppReviewData
import pl.gov.mc.protegosafe.data.model.ClearData
import pl.gov.mc.protegosafe.data.model.CloseAppData
import pl.gov.mc.protegosafe.data.model.DeleteActivitiesData
import pl.gov.mc.protegosafe.data.model.InteroperabilityData
import pl.gov.mc.protegosafe.data.model.PinData
import pl.gov.mc.protegosafe.data.model.TriageData
import pl.gov.mc.protegosafe.domain.model.AppReviewItem
import pl.gov.mc.protegosafe.domain.model.ChangeServiceStatusRequestMapper
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem
import pl.gov.mc.protegosafe.domain.model.ClearItem
import pl.gov.mc.protegosafe.domain.model.CloseAppItem
import pl.gov.mc.protegosafe.domain.model.DeleteActivitiesItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.model.InteroperabilityItem
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.TriageItem

class IncomingBridgePayloadMapperImpl(
    private val changeServiceStatusRequestMapper: ChangeServiceStatusRequestMapper
) : IncomingBridgePayloadMapper {

    override fun toTriageItem(payload: String): TriageItem {
        return Gson().fromJson(payload, TriageData::class.java).toEntity()
    }

    override fun toClearItem(payload: String): ClearItem {
        return Gson().fromJson(payload, ClearData::class.java).toEntity()
    }

    override fun toLanguageISO(payload: String): String {
        return Gson().fromJson(payload, AppLanguageData::class.java).language
    }

    override fun toChangeStatusRequestItemList(payload: String): List<ChangeStatusRequestItem> {
        return changeServiceStatusRequestMapper.toDomain(payload)
    }

    override fun toCloseAppItem(payload: String): CloseAppItem {
        return Gson().fromJson(payload, CloseAppData::class.java).toEntity()
    }

    override fun toPinItem(payload: String): PinItem {
        return Gson().fromJson(payload, PinData::class.java).toEntity()
    }

    override fun toInteroperabilityItem(payload: String): InteroperabilityItem {
        return Gson().fromJson(payload, InteroperabilityData::class.java).toEntity()
    }

    override fun toAppReviewItem(payload: String): AppReviewItem {
        return Gson().fromJson(payload, AppReviewData::class.java).toEntity()
    }

    override fun toDeleteActivities(payload: String): DeleteActivitiesItem {
        return Gson().fromJson(payload, DeleteActivitiesData::class.java).toEntity()
    }
}
