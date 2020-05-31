package pl.gov.mc.protegosafe.data.cloud

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.File

interface DiagnosisKeyDownloadService {
    //"%2F" is a workaround for double "//" (slashes) before index.txt
    @GET("%2Findex.txt")
    @CheckReturnValue
    fun getIndex(): Single<ResponseBody>

    @GET("{fileName}")
    fun download(@Path("fileName") fileName: String): Observable<Response<ResponseBody>>
}

fun DiagnosisKeyDownloadService.downloadToFile(fileName: String, file: File): Completable {
    return download(fileName)
        .flatMapCompletable { response ->
            saveToFile(response, file)
        }
}

private fun saveToFile(response: Response<ResponseBody>, file: File): Completable {
    return Completable.fromCallable {

        Okio.buffer(Okio.sink(file)).use { sink ->
            sink.writeAll(response.body()!!.source())
        }
        return@fromCallable Completable.complete()
    }
}
