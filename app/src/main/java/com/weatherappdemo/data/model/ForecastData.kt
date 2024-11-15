package com.weatherappdemo.data.model

import com.weatherappdemo.data.remote.webResponse.ForecastResponse
import com.weatherappdemo.utils.LogUtils
import com.weatherappdemo.utils.Utils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ForecastData(
    val dateTime: String,
    val temperature: Double,
    val feelsLike: Double,
    var minTemperature: Double,
    var maxTemperature: Double,
    val pressure: Int,
    val humidity: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val icon: String,
    val windSpeed: Double,
    val windDirection: Int,
    val cloudiness: Int,
    val visibility: Int,
    val probabilityOfPrecipitation: Double
)

fun ForecastResponse.toForecastDataModelList(): List<ForecastData> {
    return list.map { forecastItem ->
        ForecastData(
            dateTime = forecastItem.dt_txt,
            temperature = forecastItem.main.temp,
            feelsLike = forecastItem.main.feels_like,
            minTemperature = forecastItem.main.temp_min,
            maxTemperature = forecastItem.main.temp_max,
            pressure = forecastItem.main.pressure,
            humidity = forecastItem.main.humidity,
            weatherMain = forecastItem.weather.firstOrNull()?.main ?: "N/A",
            weatherDescription = forecastItem.weather.firstOrNull()?.description ?: "N/A",
            icon = forecastItem.weather.firstOrNull()?.icon ?: "",
            windSpeed = forecastItem.wind.speed,
            windDirection = forecastItem.wind.deg,
            cloudiness = forecastItem.clouds.all,
            visibility = forecastItem.visibility,
            probabilityOfPrecipitation = forecastItem.pop
        )
    }


}

fun ForecastResponse.toHourlyForecastDataModelList(): List<ForecastData> {
    val groupedByDay = list.groupBy {
        it.dt_txt.substring(0, 10) // Group by date (YYYY-MM-DD)
    }

    return groupedByDay.map { (date, forecastItems) ->
        // You can choose a specific time of day if you like, e.g., 12:00 PM
        val midDayForecast = forecastItems.firstOrNull {
            it.dt_txt.contains("12:00:00")
        } ?: forecastItems.first() // Fallback to the first entry of the day

        ForecastData(
            dateTime = midDayForecast.dt_txt,
            temperature = midDayForecast.main.temp,
            feelsLike = midDayForecast.main.feels_like,
            minTemperature = midDayForecast.main.temp_min,
            maxTemperature = midDayForecast.main.temp_max,
            pressure = midDayForecast.main.pressure,
            humidity = midDayForecast.main.humidity,
            weatherMain = midDayForecast.weather.firstOrNull()?.main ?: "N/A",
            weatherDescription = midDayForecast.weather.firstOrNull()?.description ?: "N/A",
            icon = midDayForecast.weather.firstOrNull()?.icon ?: "",
            windSpeed = midDayForecast.wind.speed,
            windDirection = midDayForecast.wind.deg,
            cloudiness = midDayForecast.clouds.all,
            visibility = midDayForecast.visibility,
            probabilityOfPrecipitation = midDayForecast.pop
        )
    }
}


/*
* show 5 days forecast*/
fun ForecastResponse.toWeeklyForecastDataModelList(): List<ForecastData> {
    LogUtils.log("forecastData: toWeeklyForecastDataModelList()")
    val groupedByDay = list.groupBy {
        it.dt_txt.substring(0, 10) // Group by date (YYYY-MM-DD)
    }

    val weeklyForecast = groupedByDay.map { (date, forecastItems) ->
        // Select midday forecast or fallback to the first entry of the day
        val midDayForecast = forecastItems.firstOrNull {
            it.dt_txt.contains("12:00:00")
        } ?: forecastItems.first()

        ForecastData(
            dateTime = Utils.formatToDayOfWeek(midDayForecast.dt_txt),
            temperature = midDayForecast.main.temp,
            feelsLike = midDayForecast.main.feels_like,
            minTemperature = midDayForecast.main.temp_min,
            maxTemperature = midDayForecast.main.temp_max,
            pressure = midDayForecast.main.pressure,
            humidity = midDayForecast.main.humidity,
            weatherMain = midDayForecast.weather.firstOrNull()?.main ?: "N/A",
            weatherDescription = midDayForecast.weather.firstOrNull()?.description ?: "N/A",
            icon = midDayForecast.weather.firstOrNull()?.icon ?: "",
            windSpeed = midDayForecast.wind.speed,
            windDirection = midDayForecast.wind.deg,
            cloudiness = midDayForecast.clouds.all,
            visibility = midDayForecast.visibility,
            probabilityOfPrecipitation = midDayForecast.pop
        )
    }.toMutableList()

    val todayForecast = list.firstOrNull()?.let { todayData ->
        ForecastData(
            dateTime = "Today",
            temperature = todayData.main.temp,
            feelsLike = todayData.main.feels_like,
            minTemperature = todayData.main.temp_min,
            maxTemperature = todayData.main.temp_max,
            pressure = todayData.main.pressure,
            humidity = todayData.main.humidity,
            weatherMain = todayData.weather.firstOrNull()?.main ?: "N/A",
            weatherDescription = todayData.weather.firstOrNull()?.description ?: "N/A",
            icon = todayData.weather.firstOrNull()?.icon ?: "",
            windSpeed = todayData.wind.speed,
            windDirection = todayData.wind.deg,
            cloudiness = todayData.clouds.all,
            visibility = todayData.visibility,
            probabilityOfPrecipitation = todayData.pop
        )
    }

    todayForecast?.let { weeklyForecast.add(0, it) }

    return weeklyForecast
}



