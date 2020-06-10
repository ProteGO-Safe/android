package pl.gov.mc.protegosafe.domain.model

sealed class SafetyNetResult {
    object Success : SafetyNetResult()
    sealed class Failure : SafetyNetResult() {
        object SafetyError : Failure()
        object GooglePlayServicesNotAvailable : Failure()
        class UnknownError(val exception: Exception?) : Failure()
    }
}
