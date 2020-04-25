package pl.gov.mc.protegosafe.domain.usecase.auth

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.AuthRepository
import pl.gov.mc.protegosafe.domain.usecase.GetTemporaryIDUseCase

class SignInUseCase(
    private val authRepository: AuthRepository,
    private val postExecutionThread: PostExecutionThread,
    private val getTemporaryIDUseCase: GetTemporaryIDUseCase
) {
    fun execute(): Completable {
        return authRepository.signIn()
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}