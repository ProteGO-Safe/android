package pl.gov.mc.protegosafe.domain.model

interface OutgoingBridgeDataResultComposer {
    fun composeTemporaryExposureKeysUploadResult(state: TemporaryExposureKeysUploadState): String
    fun composeAppLifecycleStateResult(state: AppLifecycleState): String
    fun composeAnalyzeResult(
        riskLevelConfigurationItem: RiskLevelConfigurationItem,
        exposure: ExposureItem
    ): String

    fun composeAppVersionNameResult(versionName: String): String
    fun composeSystemLanguageResult(languageISO: String): String
    fun composeDistrictsRestrictionsResult(
        voivodeships: List<VoivodeshipItem>,
        updated: Long
    ): String
}
