package pl.gov.mc.protegosafe.domain.model

data class DistrictActionItem(
    val type: ActionType,
    val districtId: Int
) {
    enum class ActionType(val value: Int) {
        ADD(1),
        DELETE(2);

        companion object {
            fun valueOf(value: Int): ActionType =
                values().find { it.value == value }
                    ?: throw IllegalStateException("Illegal action type")
        }
    }
}
