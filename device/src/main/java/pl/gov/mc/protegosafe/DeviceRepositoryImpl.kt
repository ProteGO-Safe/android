package pl.gov.mc.protegosafe

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.repository.TrackingRepository
import pl.gov.mc.protegosafe.model.ServicesStatus
import pl.gov.mc.protegosafe.model.ServicesStatusRoot
import pub.devrel.easypermissions.EasyPermissions

class DeviceRepositoryImpl(
    private val context: Context,
    private val trackingRepository: TrackingRepository
) : DeviceRepository {

    override fun isBtSupported(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    override fun isLocationEnabled(): Boolean {
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun isBtOn(): Boolean {
        return (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)
            ?.adapter
            ?.isEnabled == true
    }

    @SuppressLint("BatteryLife")
    override fun isBatteryOptimizationOn(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.packageName?.let { packageName ->
                Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).let { intent ->
                    intent.data = Uri.parse("package:$packageName")
                    context.packageManager?.let {
                        if (intent.resolveActivity(it) == null) {
                            return false
                        }
                    }
                }
            }
            (context.getSystemService(AppCompatActivity.POWER_SERVICE) as? PowerManager)
                ?.isIgnoringBatteryOptimizations(context.packageName) == false
        } else {
            false
        }
    }

    override fun isNotificationEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    override fun isBtServiceOn(): Boolean {
        return trackingRepository.isTrackingAccepted()
    }

    override fun getServicesStatusJson(): String {
        val servicesStatus = ServicesStatusRoot(
            ServicesStatus(
                isBtSupported = isBtSupported(),
                isLocationEnabled = isLocationEnabled(),
                isBtOn = isBtOn(),
                isBatteryOptimizationOn = isBatteryOptimizationOn(),
                isNotificationEnabled = isNotificationEnabled(),
                isBtServiceOn = isBtServiceOn()
            )
        )
        return Gson().toJson(servicesStatus)
    }
}