package com.weatherappdemo.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationHelper(
    private val activity: Activity,
    private val locationPermissionLauncher: ActivityResultLauncher<String>
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    fun checkAndRequestLocationPermission(onPermissionGranted: (Pair<Double, Double>) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation(onPermissionGranted)
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun fetchLocation(onPermissionGranted: (Pair<Double, Double>) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    LogUtils.log("Location fetched: ${location.latitude}, ${location.longitude}")
                    onPermissionGranted(Pair(location.latitude, location.longitude))
                } else {
                    LogUtils.log("Location not available")
                }
            }.addOnFailureListener {
                LogUtils.log("Failed to get location: ${it.message}")
            }
        }
    }
}

