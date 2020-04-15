package se.sigmaconnectivity.blescanner.domain

import io.reactivex.Completable
import io.reactivex.Single

interface UserRepository {
    fun getUserHash(): Single<String>
    fun saveUserHash(hash: String): Completable
}