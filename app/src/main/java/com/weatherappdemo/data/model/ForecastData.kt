package com.weatherappdemo.data.model

import com.weatherappdemo.data.remote.webResponse.ForecastResponse

data class ForecastData(
    val dateTime: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
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

fun ForecastResponse.toWeeklyForecastDataModelList(): List<ForecastData> {
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

