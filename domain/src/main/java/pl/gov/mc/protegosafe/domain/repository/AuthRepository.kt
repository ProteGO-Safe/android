package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable

interface AuthRepository {
    fun signIn() : Completable
}