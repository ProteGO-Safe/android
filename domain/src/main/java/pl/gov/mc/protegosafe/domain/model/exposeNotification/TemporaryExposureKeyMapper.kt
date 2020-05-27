package pl.gov.mc.protegosafe.domain.model.exposeNotification

fun TemporaryExposureKeyItem.toDiagnosisKey() =
    DiagnosisKey(key = key, intervalNumber = rollingStartNumber)

fun List<TemporaryExposureKeyItem>.toDiagnosisKeyList() =
    map { it.toDiagnosisKey() }
