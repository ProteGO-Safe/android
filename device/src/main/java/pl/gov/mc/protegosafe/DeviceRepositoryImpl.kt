package pl.gov.mc.protegosafe

import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationStatusItem
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.model.ServicesStatus
import pl.gov.mc.protegosafe.model.ServicesStatusRoot

class DeviceRepositoryImpl(
    private val context: Context,
    private val exposureNotificationRepository: ExposureNotificationRepository
) : DeviceRepository {

    override fun getServicesStatusJson(): Single<String> {
        return getExposureNotificationStatus()
            .map {
                val servicesStatus = ServicesStatusRoot(
                    ServicesStatus(
                        isNotificationEnabled = isNotificationEnabled(),
                        exposureNotificationStatus = it.value,
                        isBtOn = isBtOn(),
                        isLocationOn = isLocationOn()
                    )
                )
                Gson().toJson(servicesStatus)
            }
    }

    override fun getExposureNotificationStatus(): Single<ExposureNotificationStatusItem> {
        return exposureNotificationRepository.getExposureNotificationState()
    }

    private fun isBtOn(): Boolean {
        return (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)
            ?.adapter
            ?.isEnabled == true
    }

    private fun isNotificationEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun isLocationOn(): Boolean {
        return (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
            ?.isProviderEnabled(LocationManager.GPS_PROVIDER)
            ?: false
    }
}
