package pl.gov.mc.protegosafe.domain.repository

interface KeyUploadSystemInfoRepository {
    val platform: String
    val appPackageName: String
    val regions: List<String>
}
