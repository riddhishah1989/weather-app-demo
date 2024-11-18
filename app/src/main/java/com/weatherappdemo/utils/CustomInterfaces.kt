package com.weatherappdemo.utils

import com.weatherappdemo.data.model.WeatherDataModel

object CustomInterfaces {
    interface OnSearchedCityItemClick {
        fun onItemClick(data: WeatherDataModel)
    }
}