package com.duakelinci.core.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import androidx.lifecycle.LiveData
import com.duakelinci.core.R
import timber.log.Timber

class GpsStatusLiveData(private val context: Context): LiveData<GpsStatus>() {
    private val gpsSwitchStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = checkGpsAndReact()
    }

    override fun onInactive() = unregisterReceiver()
    override fun onActive() {
        registerReceiver()
        checkGpsAndReact()
    }

    private fun checkGpsAndReact() = if (isLocationEnabled()) {
        postValue(GpsStatus.Enabled())
    } else {
        postValue(GpsStatus.Disabled())
    }

    private fun isLocationEnabled() = try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    } catch (e: Throwable){
        Timber.e(e)
        false
    }


    /*private fun isLocationEnabled() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getSystemService(LocationManager::class.java)
            .isProviderEnabled(LocationManager.GPS_PROVIDER)
    } else {
        try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.LOCATION_MODE
            ) != Settings.Secure.LOCATION_MODE_OFF
        } catch (e: Settings.SettingNotFoundException) {
            Timber.e(e)
            false
        }
    }*/

    /**
     * Broadcast receiver to listen the Location button toggle state in Android.
     */
    private fun registerReceiver() = context.registerReceiver(gpsSwitchStateReceiver,
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
    )

    private fun unregisterReceiver() = context.unregisterReceiver(gpsSwitchStateReceiver)
}

sealed class GpsStatus {
    data class Enabled(val message: Int = R.string.base_gps_on) : GpsStatus()
    data class Disabled(val message: Int = R.string.base_gps_off) : GpsStatus()
}