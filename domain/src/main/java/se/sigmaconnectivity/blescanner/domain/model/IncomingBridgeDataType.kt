package se.sigmaconnectivity.blescanner.domain.model

enum class IncomingBridgeDataType(val code: Int) {
    HASH_ID(0);


    companion object {
        fun valueOf(value: Int): IncomingBridgeDataType = IncomingBridgeDataType.values().find { it.code == value } ?: throw IllegalAccessException()
    }
}