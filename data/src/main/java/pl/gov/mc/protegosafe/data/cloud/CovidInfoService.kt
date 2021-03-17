package pl.gov.mc.protegosafe.data.cloud

import io.reactivex.Single
import okhttp3.ResponseBody
import pl.gov.mc.protegosafe.data.Consts
import pl.gov.mc.protegosafe.data.model.TimestampsResponseData
import retrofit2.http.GET
import retrofit2.http.Query

interface CovidInfoService {

    /**
     * @param randomSeed random string to get new data from CDN instantly
     * Returns last data update timestamps
     */
    @GET(Consts.CovidInfoJson.TIMESTAMPS)
    fun getTimestamps(@Query("randomSeed") randomSeed: String): Single<TimestampsResponseData>

    /**
     * @param randomSeed random string to get new data from CDN instantly
     * Returns basic statistics
     */
    @GET(Consts.CovidInfoJson.DASHBOARD)
    fun getDashboard(@Query("randomSeed") randomSeed: String): Single<ResponseBody>

    /**
     * @param randomSeed random string to get new data from CDN instantly
     * Returns detailed statistics
     */
    @GET(Consts.CovidInfoJson.DETAILS)
    fun getDetails(@Query("randomSeed") randomSeed: String): Single<ResponseBody>

    /**
     * @param randomSeed random string to get new data from CDN instantly
     * Returns voivodeships with districts
     */
    @GET(Consts.CovidInfoJson.DISTRICTS)
    fun getVoivodeships(@Query("randomSeed") randomSeed: String): Single<ResponseBody>
}
