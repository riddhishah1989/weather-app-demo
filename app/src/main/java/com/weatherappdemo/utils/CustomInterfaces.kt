package com.weatherappdemo.utils

import com.weatherappdemo.data.model.WeatherData

object CustomInterfaces {
    interface OnSearchedCityItemClick {
        fun onItemClick(data: WeatherData)
    }
}