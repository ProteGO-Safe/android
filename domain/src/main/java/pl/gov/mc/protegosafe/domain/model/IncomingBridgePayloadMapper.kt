package pl.gov.mc.protegosafe.domain.model

interface IncomingBridgePayloadMapper {
    fun toTriageItem(payload: String): TriageItem

    fun toClearItem(payload: String): ClearItem

    /**
     * @return String in ISO 639-1 standard
     */
    fun toLanguageISO(payload: String): String

    fun toChangeStatusRequestItemList(payload: String): List<ChangeStatusRequestItem>
}
