package pl.gov.mc.protegosafe.domain.model

enum class RiskLevelItem(val value: Int) {
    NO_RISK(0),
    LOW_RISK(1),
    MIDDLE_RISK(2),
    HIGH_RISK(3);

    companion object {
        fun valueOf(value: Int): RiskLevelItem =
            RiskLevelItem.values().find { it.value == value } ?: RiskLevelItem.NO_RISK
    }
}
