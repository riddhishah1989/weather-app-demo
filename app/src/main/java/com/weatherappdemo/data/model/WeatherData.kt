package com.weatherappdemo.data.model

import com.weatherappdemo.data.remote.webResponse.WeatherResponse

data class WeatherData(
    val cityName: String,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val cloudiness: Int,
    val visibility: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
    val weatherMain: String,
    val description: String,
    val icon: String
)


// Mapping function
fun WeatherResponse.toWeatherData() = WeatherData(
    cityName = this.name,
    temperature = this.main.temp,
    feelsLike = this.main.feels_like,
    tempMin = this.main.temp_min,
    tempMax = this.main.temp_max,
    pressure = this.main.pressure,
    humidity = this.main.humidity,
    windSpeed = this.wind.speed,
    windDirection = this.wind.deg,
    cloudiness = this.clouds.all,
    visibility = this.visibility,
    country = this.sys.country,
    sunrise = this.sys.sunrise,
    sunset = this.sys.sunset,
    weatherMain = this.weather.firstOrNull()?.main ?: "N/A",
    description = this.weather.firstOrNull()?.description ?: "N/A",
    icon = this.weather.firstOrNull()?.icon ?: "N/A"
)



