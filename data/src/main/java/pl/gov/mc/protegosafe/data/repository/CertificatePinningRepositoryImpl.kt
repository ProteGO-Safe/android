package pl.gov.mc.protegosafe.data.repository

import android.content.Context
import com.datatheorem.android.trustkit.TrustKit
import pl.gov.mc.protegosafe.domain.repository.CertificatePinningRepository

class CertificatePinningRepositoryImpl(private val context: Context) : CertificatePinningRepository {
    override fun initialize() {
        TrustKit.initializeWithNetworkSecurityConfiguration(context)
    }
}
