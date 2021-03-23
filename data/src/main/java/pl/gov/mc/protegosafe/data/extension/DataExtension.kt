package pl.gov.mc.protegosafe.data.extension

import com.google.gson.Gson

fun Any.toJson(gson: Gson = Gson()): String = gson.toJson(this)
