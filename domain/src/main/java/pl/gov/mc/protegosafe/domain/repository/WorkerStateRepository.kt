package pl.gov.mc.protegosafe.domain.repository

interface WorkerStateRepository {
    var shouldProvideDiagnosisKeysWorkerStartOnBoot: Boolean
}
