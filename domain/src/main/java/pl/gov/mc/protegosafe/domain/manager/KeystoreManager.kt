package pl.gov.mc.protegosafe.domain.manager

import javax.crypto.SecretKey

interface KeystoreManager {
    fun getSecretKey(alias: String): SecretKey
}
