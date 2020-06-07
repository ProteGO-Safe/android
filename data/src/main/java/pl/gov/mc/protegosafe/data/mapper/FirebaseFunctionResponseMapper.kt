package pl.gov.mc.protegosafe.data.mapper

import com.google.firebase.functions.HttpsCallableResult
import com.google.gson.Gson
import org.json.JSONObject
import pl.gov.mc.protegosafe.data.model.GetAccessTokenResponseBody

fun HttpsCallableResult.toGetAccessTokenResponseBody(): GetAccessTokenResponseBody =
    toJson().let { Gson().fromJson(it, GetAccessTokenResponseBody::class.java) }

private fun HttpsCallableResult.toJson(): String =
    (data as? Map<*, *>)?.let { JSONObject(it).toString() }
        ?: throw Exception("Unable to parse data to json $data")
