package pl.gov.mc.protegosafe.data.mapper

import pl.gov.mc.protegosafe.domain.model.ConnectionException
import pl.gov.mc.protegosafe.domain.model.RetrofitExceptionMapper
import retrofit2.HttpException
import kotlin.Exception

class RetrofitExceptionMapperImpl : RetrofitExceptionMapper {
    override fun toConnectionError(exception: Exception): Exception {
        return (exception as? HttpException)?.let {
            when (it.code()) {
                NOT_FOUND_CODE -> ConnectionException.NotFound
                else -> ConnectionException.Other
            }
        } ?: exception
    }
}

private const val NOT_FOUND_CODE = 404
