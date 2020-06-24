package pl.gov.mc.protegosafe.data.extension

import com.google.gson.Gson

fun Any.toJson(): String = Gson().toJson(this)
