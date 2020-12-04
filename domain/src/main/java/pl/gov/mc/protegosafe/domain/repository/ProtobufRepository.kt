package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Single
import java.io.File

interface ProtobufRepository {
    fun getTemporaryExposureKeysAmount(files: List<File>): Single<Long>
}
