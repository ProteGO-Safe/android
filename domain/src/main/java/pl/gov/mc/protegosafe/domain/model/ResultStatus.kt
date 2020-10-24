package pl.gov.mc.protegosafe.domain.model

enum class ResultStatus(val value: Int) {
    SUCCESS(1),
    FAILURE(2),
    CANCELED(3);
}
