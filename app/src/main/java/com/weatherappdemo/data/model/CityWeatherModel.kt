package com.weatherappdemo.data.model

import com.weatherappdemo.data.local.entities.WeatherEntity
import com.weatherappdemo.data.remote.webResponse.CityWeatherResponse

data class CityWeatherModel(
    val cityName: String,
    var temperature: Double,
    var feelsLike: Double,
    var tempMin: Double,
    var tempMax: Double,
    var pressure: Int,
    var humidity: Int,
    var windSpeed: Double,
    val windDirection: Int,
    val cloudiness: Int,
    val visibility: Int,
    val country: String,
    var sunrise: Long,
    var sunset: Long,
    val weatherMain: String,
    val description: String,
    val icon: String,
    var latitude: Double,
    var longitude: Double
)


// Mapping function
fun CityWeatherResponse.toCityWeatherData() = CityWeatherModel(
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
    icon = this.weather.firstOrNull()?.icon ?: "N/A",
    longitude = this.coord.lat,
    latitude = this.coord.lon
)

fun CityWeatherModel.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.cityName,
        temperature = this.temperature,
        latitude = this.latitude,
        longitude = this.longitude,
        weatherDescription = this.description,
        humidity = this.humidity,
        windSpeed = this.windSpeed,
        tempMax = this.tempMax,
        tempMin = this.tempMin,
        feelLike = this.feelsLike,
        sunriseTime = this.sunrise,
        sunsetTime = this.sunset,
        lastUpdated = System.currentTimeMillis(), // Add the current timestamp
    )
}
