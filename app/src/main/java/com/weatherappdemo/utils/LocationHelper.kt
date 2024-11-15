package com.weatherappdemo.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class LocationHelper(
    private val activity: Activity,
    private val locationPermissionLauncher: ActivityResultLauncher<String>,
    private val onLocationChanged: (latitude: Double, longitude: Double) -> Unit
) {
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    fun checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchCurrentLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun fetchCurrentLocation() {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertMessage()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location != null) {
                LogUtils.log(
                    "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                )
                onLocationChanged(location.latitude, location.longitude)
            } else {
                Utils.showToast(activity, "Unable to fetch location. Try again later.")
            }
        }.addOnFailureListener { exception ->
            Utils.showToast(
                activity, "Error fetching location: ${exception.message}"
            )
        }
    }

    private fun showAlertMessage() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("The location service is disabled. Do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        builder.create().show()
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates {
            LogUtils.log("Location updates stopped.")
        }
    }
}