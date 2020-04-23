package pl.gov.mc.protegosafe

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository
import pl.gov.mc.protegosafe.model.ServicesStatus

class DeviceRepositoryImpl(
    private val context: Context,
    private val openTraceRepository: OpenTraceRepository
) : DeviceRepository {

    //TODO: prepare broadcast receiver to track service status changes
    private  val traceServiceEnabledSubject: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false)

    override val traceServiceEnabled: Observable<Boolean> = traceServiceEnabledSubject.hide()

    override fun isBtSupported(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    override fun isLocationEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
                ?.isLocationEnabled == true
        } else {
            @Suppress("DEPRECATION")
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE
            ) != Settings.Secure.LOCATION_MODE_OFF
        }
    }

    override fun isBtOn(): Boolean {
        return (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)
            ?.adapter
            ?.isEnabled == true
    }

    override fun isBatteryOptimizationOn(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context.getSystemService(AppCompatActivity.POWER_SERVICE) as? PowerManager)
                ?.isIgnoringBatteryOptimizations(context.packageName) == true
        } else {
            true
        }
    }

    override fun isNotificationEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    override fun isBtServiceOn(): Boolean {
        return openTraceRepository.getBLEServiceStatus()
    }

    override fun getServicesStatusJson(): String {
        val servicesStatus = ServicesStatus(
            isBtSupported = isBtSupported(),
            isLocationEnabled = isLocationEnabled(),
            isBtOn = isBtOn(),
            isBatteryOptimizationOn = isBatteryOptimizationOn(),
            isNotificationEnabled = isNotificationEnabled(),
            isBtServiceOn = isBtServiceOn()
        )
        return Gson().toJson(servicesStatus)
    }
}