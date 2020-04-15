package se.sigmaconnectivity.blescanner.data

import io.reactivex.Completable
import io.reactivex.Single
import se.sigmaconnectivity.blescanner.data.db.UserIdStore
import se.sigmaconnectivity.blescanner.domain.UserRepository

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