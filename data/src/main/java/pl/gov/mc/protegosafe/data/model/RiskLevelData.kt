package pl.gov.mc.protegosafe.data.model

enum class RiskLevelData(val value: Int) {
    NO_RISK(1),
    MIDDLE_RISK(2),
    HIGH_RISK(3);

    companion object {
        fun fromRiskScore(riskScore: Int): RiskLevelData {
            return when {
                riskScore < 1500 -> NO_RISK
                riskScore < 3000 -> MIDDLE_RISK
                else -> HIGH_RISK
            }
        }
    }
}
