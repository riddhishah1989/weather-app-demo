package com.weatherappdemo.data.remote.webResponse

import com.weatherappdemo.data.model.WeatherData

data class WeatherResponse(
    val status: String,
    val message: String,
    val data: WeatherData
)


