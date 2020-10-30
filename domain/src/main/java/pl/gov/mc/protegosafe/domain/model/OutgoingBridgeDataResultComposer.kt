package pl.gov.mc.protegosafe.domain.model

interface OutgoingBridgeDataResultComposer {
    fun composeTemporaryExposureKeysUploadResult(state: TemporaryExposureKeysUploadState): String
    fun composeAppLifecycleStateResult(state: AppLifecycleState): String
    fun composeAnalyzeResult(riskLevelItem: RiskLevelItem): String
    fun composeAppVersionNameResult(versionName: String): String
    fun composeSystemLanguageResult(languageISO: String): String
    fun composeFontScaleResult(fontScale: Float): String
    fun composeDistrictsRestrictionsResult(
        voivodeships: List<VoivodeshipItem>,
        updated: Long
    ): String
    fun composeSubscribedDistrictsResult(subscribedDistricts: List<DistrictItem>): String
    fun composeUploadTestPinResult(resultStatus: ResultStatus): String
    fun composeTestSubscriptionStatusResult(
        testSubscriptionItem: TestSubscriptionItem?
    ): String
    fun composeTestSubscriptionPinResult(pin: String): String
}
