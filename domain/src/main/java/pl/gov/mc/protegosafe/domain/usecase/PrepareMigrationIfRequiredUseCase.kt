package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.MigrationRepository

class PrepareMigrationIfRequiredUseCase(
    private val migrationRepository: MigrationRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(currentVersionName: String): Completable {
        return migrationRepository.prepareMigrationUrlIfRequired()
            .andThen(
                Completable.fromAction {
                    migrationRepository.updateCurrentVersion(
                        currentVersionName
                    )
                }
            )
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
