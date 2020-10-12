package pl.gov.mc.protegosafe.data.cloud

import io.reactivex.Single
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.model.CovidInfoResponseData
import retrofit2.http.GET

interface CovidInfoService {
    /**
     * Returns list of all voivodeships containing restrictions statuses for theirs districts
     */
    @GET(BuildConfig.COVID_INFO_FILE)
    fun getCovidInfo(): Single<CovidInfoResponseData>
}
