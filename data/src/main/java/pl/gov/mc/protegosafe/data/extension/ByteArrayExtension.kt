package pl.gov.mc.protegosafe.data.extension

import com.google.common.io.BaseEncoding

fun ByteArray.toBase64(): String =
    BaseEncoding.base64().encode(this)
