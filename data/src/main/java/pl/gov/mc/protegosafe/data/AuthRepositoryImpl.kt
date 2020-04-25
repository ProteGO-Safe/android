package pl.gov.mc.protegosafe.data

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.repository.AuthRepository
import timber.log.Timber

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun signIn(): Completable {
        return if (firebaseAuth.currentUser != null) {
            Timber.d("User already signed in")
            Completable.complete()
        } else {
            Completable.create { emitter ->
                firebaseAuth.signInAnonymously()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Timber.d("User signed in anonymously ")
                            emitter.onComplete()
                        } else {
                            Timber.e(it.exception, "Sign in failed")
                            emitter.onError(it.exception as Throwable)
                        }
                    }
            }
        }
    }
}