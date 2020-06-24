package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.MigrationRepository

class GetMigrationUrlUseCase(
    private val migrationRepository: MigrationRepository
) {
    fun execute(): Single<String> {
        return Single.fromCallable { return@fromCallable migrationRepository.getMigrationUrlAndClear() }
    }
}
