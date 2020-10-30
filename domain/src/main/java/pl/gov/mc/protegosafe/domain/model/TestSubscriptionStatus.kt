package pl.gov.mc.protegosafe.domain.model

import java.lang.IllegalStateException

enum class TestSubscriptionStatus(val value: Int) {
    VERIFIED(1),
    SCHEDULED(2);

    companion object {
        fun valueOf(value: Int): TestSubscriptionStatus =
            values().find { it.value == value }
                ?: throw IllegalStateException()
    }
}
