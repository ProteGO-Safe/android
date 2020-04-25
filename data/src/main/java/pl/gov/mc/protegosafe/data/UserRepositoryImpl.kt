package pl.gov.mc.protegosafe.data

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.db.UserIdStore
import pl.gov.mc.protegosafe.domain.repository.UserRepository

class UserRepositoryImpl(
    private  val userIdStore: UserIdStore
) : UserRepository {

    override fun getUserHash(): Single<String> = Single.fromCallable {
        userIdStore.userHash
    }

    override fun saveUserHash(hash: String) = Completable.fromAction {
        userIdStore.userHash = hash
    }
}