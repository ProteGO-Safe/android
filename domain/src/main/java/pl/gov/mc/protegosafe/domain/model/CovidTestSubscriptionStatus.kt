package pl.gov.mc.protegosafe.domain.model

import java.lang.IllegalStateException

enum class CovidTestSubscriptionStatus(val value: Int) {
    NOT_VERIFIED(0),
    VERIFIED(1),
    USED(2);

    companion object {
        fun valueOf(value: Int): CovidTestSubscriptionStatus =
            values().find { it.value == value }
                ?: throw IllegalStateException()
    }
}
