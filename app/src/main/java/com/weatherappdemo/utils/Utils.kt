package com.weatherappdemo.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.weatherappdemo.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object Utils {

    fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity.currentFocus
        currentFocus?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

    fun showDayFromCurrentDate(): String {
        return SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    }

    fun getWeatherIconUrl(iconName: String): String {
        return String.format(AppConstants.WEATHER_ICON_URL, iconName)
    }

    fun formatTemperature(context: Context, temperature: Double): String {
        val celcious = context.getString(R.string.degree_symbol_celcious)
        return String.format("%.1f$celcious", temperature)
    }

    fun formatToDayOfWeek(dateTime: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateTime)
        val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dayOfWeek = dayOfWeekFormat.format(date)

        val calendar = Calendar.getInstance()
        calendar.time = date
        return when (Calendar.getInstance()
            .get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR)) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> dayOfWeek
        }
    }

    fun convertUnixToAmPm(unixTime: Long): String {
        val date = Date(unixTime * 1000) // Convert seconds to milliseconds
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault()) // Format for AM/PM
        return sdf.format(date)
    }
}