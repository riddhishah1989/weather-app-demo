// WeatherConversionUtils.kt
package com.example.yourapp.utils

import com.weatherappdemo.data.local.entities.WeatherEntity
import com.weatherappdemo.data.model.Main
import com.weatherappdemo.data.model.Weather
import com.weatherappdemo.data.model.WeatherData
import com.weatherappdemo.data.model.Wind

// Function to convert a single WeatherEntity to WeatherData
fun WeatherEntity.toWeatherData(): WeatherData {
    return WeatherData(
        name = this.cityName,
        main = Main(
            temp = this.temperature,
            humidity = this.humidity
        ),
        weather = listOf(
            Weather(description = this.weatherDescription)
        ),
        wind = Wind(speed = this.windSpeed)
    )
}

// Function to convert a list of WeatherEntity to a list of WeatherData
fun List<WeatherEntity>.toWeatherDataList(): List<WeatherData> {
    return this.map { it.toWeatherData() }
}


// Convert a single WeatherData to WeatherEntity
fun WeatherData.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.name,
        temperature = this.main.temp,
        weatherDescription = this.weather[0].description, // Take the first weather description
        humidity = this.main.humidity,
        windSpeed = this.wind.speed,
        lastUpdated = System.currentTimeMillis() // Add current timestamp
    )
}

// Convert a list of WeatherData to a list of WeatherEntity
fun List<WeatherData>.toWeatherEntityList(): List<WeatherEntity> {
    return this.map { it.toWeatherEntity() }
}