package pl.gov.mc.protegosafe.domain.model

import java.lang.Exception

interface RetrofitExceptionMapper {
    fun toConnectionError(exception: Exception): Exception
}
