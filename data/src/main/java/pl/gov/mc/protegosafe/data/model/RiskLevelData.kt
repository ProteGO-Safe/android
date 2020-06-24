package pl.gov.mc.protegosafe.data.model

import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem

enum class RiskLevelData(val value: Int) {
    NO_RISK(0),
    LOW_RISK(1),
    MIDDLE_RISK(2),
    HIGH_RISK(3);

    companion object {
        fun fromRiskScore(riskLevelConfigurationItem: RiskLevelConfigurationItem, riskScore: Int): RiskLevelData {
            return when {
                riskScore <= riskLevelConfigurationItem.maxNoRiskScore -> NO_RISK
                riskScore <= riskLevelConfigurationItem.maxLowRiskScore -> LOW_RISK
                riskScore <= riskLevelConfigurationItem.maxMiddleRiskScore -> MIDDLE_RISK
                else -> HIGH_RISK
            }
        }
    }
}
