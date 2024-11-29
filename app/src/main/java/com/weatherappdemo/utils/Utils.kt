package com.weatherappdemo.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


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

    fun convertUnixToAmPm(
        utcTimestamp: Long,
        timezoneOffset: Int
    ): String {
        val adjustedTimestamp = if (timezoneOffset == 0) {
            utcTimestamp // If timezone is 0, use the timestamp directly
        } else {
            utcTimestamp + timezoneOffset
        }
        val date = Date(adjustedTimestamp * 1000)
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.timeZone = if (timezoneOffset == 0) {
            TimeZone.getDefault() // Use the device's local timezone
        } else {
            TimeZone.getTimeZone("UTC") // Use UTC adjusted by the offset
        }
        return sdf.format(date)
    }


    fun getDayDifferenceFromToday(date: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = sdf.parse(date)
        val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0) }
        val inputDate = Calendar.getInstance().apply { time = parsedDate }

        return inputDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR)
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun parseDateTimeToMillis(dateString: String): Long {
        val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return sdf.parse(dateString)?.time ?: 0L
    }

    fun isDataFresh(lastUpdated: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val freshnessThreshold = 30 * 60 * 1000 // 30 minutes in milliseconds
        return currentTime - lastUpdated < freshnessThreshold
    }
}