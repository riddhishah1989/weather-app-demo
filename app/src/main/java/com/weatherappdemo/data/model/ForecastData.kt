package com.weatherappdemo.data.model

import com.weatherappdemo.data.remote.webResponse.ForecastResponse

data class ForecastDataModel(
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

fun ForecastResponse.toForecastDataModelList(): List<ForecastDataModel> {
    return list.map { forecastItem ->
        ForecastDataModel(
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

