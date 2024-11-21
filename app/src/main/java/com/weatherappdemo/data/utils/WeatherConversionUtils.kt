package com.weatherappdemo.data.utils

import com.weatherappdemo.data.local.entities.WeatherEntity
import com.weatherappdemo.data.model.ForecastData
import com.weatherappdemo.data.model.WeatherDataModel
import com.weatherappdemo.data.remote.webResponse.ForecastResponse
import com.weatherappdemo.data.remote.webResponse.WeatherResponse
import com.weatherappdemo.utils.LogUtils
import com.weatherappdemo.utils.Utils
import com.weatherappdemo.utils.Utils.getDayDifferenceFromToday

// Conversion function from WeatherResponse to WeatherData
fun WeatherResponse.toWeatherData() = WeatherDataModel(
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
fun WeatherDataModel.toWeatherEntity(): WeatherEntity {
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
        visibility = this.visibility,
        pressure = this.pressure,
        weatherMain = this.weatherMain,
        windDirection = this.windDirection,
        icon = this.icon,
        country = this.country,
        lastUpdated = System.currentTimeMillis()
    )
}

// Conversion function from WeatherEntity to WeatherData
fun WeatherEntity.toWeatherData(): WeatherDataModel {
    return WeatherDataModel(
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
        visibility = this.visibility, // Set actual values if needed
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
fun List<WeatherEntity>.toWeatherDataList(): List<WeatherDataModel> {
    return this.map { it.toWeatherData() }
}

// Convert a list of WeatherData to a list of WeatherEntity
fun List<WeatherDataModel>.toWeatherEntityList(): List<WeatherEntity> {
    return this.map { it.toWeatherEntity() }
}

/*
* show 5 days forecast*/
fun ForecastResponse.toWeeklyForecastDataModelList(): List<ForecastData> {
    LogUtils.log("forecastData: toWeeklyForecastDataModelList()")

    val groupedByDay = list.groupBy {
        it.dt_txt.substring(0, 10) // Group by date (YYYY-MM-DD)
    }

    val weeklyForecast = mutableListOf<ForecastData>()

    groupedByDay.forEach { (date, forecastItems) ->
        // Select midday forecast or fallback to the first entry of the day
        val midDayForecast = forecastItems.firstOrNull {
            it.dt_txt.contains("12:00:00")
        } ?: forecastItems.first()

        val dayLabel = when (Utils.getDayDifferenceFromToday(date)) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> Utils.formatToDayOfWeek(midDayForecast.dt_txt)
        }

        if (weeklyForecast.none { it.dateTime == dayLabel }) {
            weeklyForecast.add(
                ForecastData(
                    dateTime = dayLabel,
                    temperature = midDayForecast.main.temp,
                    feelsLike = midDayForecast.main.feels_like,
                    minTemperature = forecastItems.minOf { it.main.temp_min },
                    maxTemperature = forecastItems.maxOf { it.main.temp_max },
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
            )
        }
    }

    return weeklyForecast.take(5) // Limit to 5 days
}


fun WeatherResponse.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = this.name,
        country = this.sys.country,
        temperature = this.main.temp,
        latitude = this.coord.lat,
        longitude = this.coord.lon,
        weatherMain = this.weather.firstOrNull()?.main ?: "N/A",
        weatherDescription = this.weather.firstOrNull()?.description ?: "N/A",
        icon = this.weather.firstOrNull()?.icon ?: "",
        humidity = this.main.humidity,
        windSpeed = this.wind.speed,
        windDirection = this.wind.deg,
        tempMax = this.main.temp_max,
        tempMin = this.main.temp_min,
        feelLike = this.main.feels_like,
        sunriseTime = this.sys.sunrise,
        sunsetTime = this.sys.sunset,
        pressure = this.main.pressure,
        visibility = this.visibility,
        lastUpdated = System.currentTimeMillis()
    )
}
