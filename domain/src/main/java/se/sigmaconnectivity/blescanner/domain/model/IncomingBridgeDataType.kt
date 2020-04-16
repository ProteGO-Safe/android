package se.sigmaconnectivity.blescanner.domain.model

enum class IncomingBridgeDataType(val code: Int) {
    TRIAGE(1);

    companion object {
        fun valueOf(value: Int): IncomingBridgeDataType = IncomingBridgeDataType.values().find { it.code == value } ?: throw IllegalAccessException()
    }
}