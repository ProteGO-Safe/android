package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.rxjava3.core.Single
import pl.gov.mc.protegosafe.domain.repository.MigrationRepository

class GetMigrationUrlUseCase(
    private val migrationRepository: MigrationRepository
) {
    fun execute(): Single<String> {
        return Single.fromCallable { return@fromCallable migrationRepository.getMigrationUrlAndClear() }
    }
}
