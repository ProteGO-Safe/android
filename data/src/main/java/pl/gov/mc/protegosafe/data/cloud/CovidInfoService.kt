package pl.gov.mc.protegosafe.data.cloud

import io.reactivex.Single
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.model.CovidInfoResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface CovidInfoService {
    /**
     * @param randomSeed random string to get new data from CDN instantly
     * Returns list of all voivodeships containing restrictions statuses for theirs districts
     */
    @GET(BuildConfig.COVID_INFO_FILE)
    fun getCovidInfo(@Query("randomSeed") randomSeed: String): Single<CovidInfoResponseData>
}
