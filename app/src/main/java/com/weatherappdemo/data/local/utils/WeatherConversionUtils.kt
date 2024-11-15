// WeatherConversionUtils.kt
package com.weatherappdemo.data.local.utils

import com.weatherappdemo.data.local.entities.WeatherEntity
import com.weatherappdemo.data.model.WeatherData
import com.weatherappdemo.data.remote.webResponse.WeatherResponse

// Conversion function from WeatherResponse to WeatherData
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
    icon = this.weather.firstOrNull()?.icon ?: "N/A",
    latitude = this.coord.lat,
    longitude = this.coord.lon
)

// Conversion function from WeatherData to WeatherEntity
fun WeatherData.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.cityName,
        temperature = this.temperature,
        latitude = 0.0,  // Set actual values if needed
        longitude = 0.0, // Set actual values if needed
        weatherDescription = this.description,
        humidity = this.humidity,
        windSpeed = this.windSpeed,
        tempMax = this.tempMax,
        tempMin = this.tempMin,
        feelLike = this.feelsLike,
        sunriseTime = this.sunrise,
        sunsetTime = this.sunset,
        lastUpdated = System.currentTimeMillis()
    )
}

// Conversion function from WeatherEntity to WeatherData
fun WeatherEntity.toWeatherData(): WeatherData {
    return WeatherData(
        cityName = this.cityName,
        temperature = this.temperature,
        feelsLike = this.feelLike,
        tempMin = this.tempMin,
        tempMax = this.tempMax,
        pressure = 0, // Set actual values if needed
        humidity = this.humidity,
        windSpeed = this.windSpeed,
        windDirection = 0, // Set actual values if needed
        cloudiness = 0, // Set actual values if needed
        visibility = 0, // Set actual values if needed
        country = "", // Set actual values if needed
        sunrise = this.sunriseTime,
        sunset = this.sunsetTime,
        weatherMain = "", // Set actual values if needed
        description = this.weatherDescription,
        icon = "", // Set actual values if needed,
        latitude = this.latitude,
        longitude = this.longitude

    )
}

// Convert a list of WeatherEntity to a list of WeatherData
fun List<WeatherEntity>.toWeatherDataList(): List<WeatherData> {
    return this.map { it.toWeatherData() }
}

// Convert a list of WeatherData to a list of WeatherEntity
fun List<WeatherData>.toWeatherEntityList(): List<WeatherEntity> {
    return this.map { it.toWeatherEntity() }
}
