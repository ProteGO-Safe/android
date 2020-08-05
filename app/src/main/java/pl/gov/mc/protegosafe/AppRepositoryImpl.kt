package pl.gov.mc.protegosafe

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.AppRepository
import java.util.Locale

class AppRepositoryImpl : AppRepository {
    override fun getVersionName(): Single<String> {
        return Single.just(BuildConfig.VERSION_NAME)
    }

    override fun getSystemLanguage(): Single<String> {
        return Single.just(Locale.getDefault().language.toUpperCase(Locale.getDefault()))
    }
}
