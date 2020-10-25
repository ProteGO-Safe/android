package pl.gov.mc.protegosafe.domain.model

import java.lang.IllegalArgumentException

enum class DistrictRestrictionStateItem(val value: Int) {
    NEUTRAL(0),
    YELLOW(1),
    RED(2);

    companion object {
        fun valueOf(value: Int): DistrictRestrictionStateItem =
            values().find { it.value == value } ?: throw IllegalArgumentException("$value")
    }
}
