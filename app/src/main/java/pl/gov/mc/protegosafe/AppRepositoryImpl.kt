package pl.gov.mc.protegosafe

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.db.AppLanguageDataStore
import pl.gov.mc.protegosafe.domain.repository.AppRepository
import java.util.Locale

class AppRepositoryImpl(
    private val appLanguageDataStore: AppLanguageDataStore,
    private val context: Context
) : AppRepository {
    override fun getVersionName(): Single<String> {
        return Single.just(BuildConfig.VERSION_NAME)
    }

    override fun getSystemLanguage(): Single<String> {
        return Single.just(Locale.getDefault().language.toUpperCase(Locale.getDefault()))
    }

    override fun getLocale(): Locale {
        return Locale(appLanguageDataStore.appLanguageISO)
    }

    override fun setAppLanguage(languageISO: String): Completable {
        return Completable.fromAction {
            appLanguageDataStore.appLanguageISO = languageISO
        }
    }

    override fun getFontScale(): Single<Float> {
        return Single.just(context.resources.configuration.fontScale)
    }
}
