package pl.gov.mc.protegosafe.data.extension

import com.google.gson.Gson

inline fun <reified T> String.fromJson(gson: Gson = Gson()): T = gson.fromJson(this, T::class.java)
