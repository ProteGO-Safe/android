package pl.gov.mc.protegosafe.data

import android.content.Context
import pl.gov.mc.protegosafe.domain.repository.KeyUploadSystemInfoRepository

class KeyUploadSystemInfoRepositoryImpl(context: Context) : KeyUploadSystemInfoRepository {
    override val platform = PLATFORM
    override val appPackageName: String = context.packageName
    override val regions: List<String> = listOf(REGION_PL)

    companion object {
        private const val PLATFORM = "android"
        private const val REGION_PL = "PL"
    }
}
