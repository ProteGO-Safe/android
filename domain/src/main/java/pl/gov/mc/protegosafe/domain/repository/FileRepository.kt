package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single

interface FileRepository {
    fun writeInternalFile(fileName: String, text: String): Completable
    fun readInternalFile(fileName: String): Single<String>
    fun readInternalFileOrEmpty(fileName: String): Single<String>
    fun clearAllInternalFiles(): Completable
}
