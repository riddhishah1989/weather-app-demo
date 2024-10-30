package com.weatherappdemo.data.remote.webResponse

import com.weatherappdemo.data.model.ForecastData

data class ForecastResponse(
    val status: String,
    val message: String,
    val data: ForecastData
)