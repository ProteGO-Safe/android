package pl.gov.mc.protegosafe

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.AppRepository

class AppRepositoryImpl : AppRepository {
    override fun getVersionName(): Single<String> {
        return Single.just(BuildConfig.VERSION_NAME)
    }
}
